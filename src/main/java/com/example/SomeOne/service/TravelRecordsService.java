package com.example.SomeOne.service;

import com.amazonaws.services.s3.AmazonS3;
import com.example.SomeOne.config.SecurityUtil;
import com.example.SomeOne.domain.*;
import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import com.example.SomeOne.dto.TravelRecords.Request.CreateTravelRecordRequest;
import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
import com.example.SomeOne.exception.ImageStorageException;
import com.example.SomeOne.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelRecordsService {

    private final TravelRecordsRepository travelRecordsRepository;
    private final BusinessReviewsRepository businessReviewsRepository;
    private final BusinessReviewImagesRepository businessReviewImagesRepository;
    private final TravelPlansRepository travelPlansRepository;
    private final TravelPlaceRepository travelPlaceRepository;
    private final RecordImagesRepository recordImagesRepository;
    private final UserRepository userRepository;
    private final S3ImageUploadService s3ImageUploadService;
    private final AmazonS3 amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 여행 기록 생성
    @Transactional
    public TravelRecordResponse create(List<MultipartFile> images, CreateTravelRecordRequest request, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 여행 계획 조회
        TravelPlans plan = travelPlansRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + request.getPlanId()));

        // 여행 계획에 이미 여행 기록이 있는지 확인
        boolean recordExists = travelRecordsRepository.existsByPlan(plan);
        if (recordExists) {
            throw new IllegalArgumentException("Travel record already exists for this travel plan.");
        }

        // 새로운 여행 기록 생성 및 저장
        TravelRecords record = TravelRecords.builder()
                .user(user)
                .plan(plan)
                .recordTitle(request.getOneLineReview())
                .recordContent(request.getOverallReview())
                .publicPrivate(request.isPublicPrivate())
                .build();

        // 이미지 저장 및 여행 기록 저장
        List<String> imageUrls = saveImages(images, record);
        TravelRecords savedRecord = travelRecordsRepository.save(record);

        // 비즈니스 리뷰 처리
        Map<LocalDate, List<BusinessReviewResponse>> businessReviewResponses = handleBusinessReviews(plan, user);

        // 응답 객체 생성 및 반환
        return new TravelRecordResponse(
                savedRecord.getRecordId(),
                savedRecord.getRecordTitle(),
                savedRecord.getRecordContent(),
                imageUrls,
                savedRecord.getPublicPrivate(),
                plan.getPlanId(),
                plan.getPlan_name(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getIsland() != null ? plan.getIsland().getName() : null,
                plan.getStatus(),
                savedRecord.getUser().getUsers_id(),
                businessReviewResponses,
                plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
        );
    }


    // 이미지 저장 메서드
    private List<String> saveImages(List<MultipartFile> images, TravelRecords record) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = s3ImageUploadService.saveImage(image);
            imageUrls.add(imageUrl);

            // RecordImages 객체 생성
            RecordImages recordImage = RecordImages.builder()
                    .imageUrl(imageUrl)
                    .record(record)
                    .build();

            // RecordImages 엔티티 저장
            recordImagesRepository.save(recordImage);

            record.addRecordImage(recordImage);  // TravelRecords에 이미지 추가
        }
        return imageUrls;
    }

    // 비즈니스 리뷰 처리 메서드 (날짜별로 그룹화된 리뷰 반환)
    private Map<LocalDate, List<BusinessReviewResponse>> handleBusinessReviews(TravelPlans plan, Users user) {
        Map<LocalDate, List<BusinessReviewResponse>> groupedReviews = new HashMap<>();
        List<TravelPlace> travelPlaces = travelPlaceRepository.findByTravelPlans(plan);

        for (TravelPlace travelPlace : travelPlaces) {
            Businesses business = travelPlace.getBusinesses();
            if (business != null) {
                Optional<BusinessReviews> existingReview = businessReviewsRepository.findByBusinessAndUser(business, user);
                BusinessReviews businessReview = existingReview.orElse(null);

                if (businessReview != null) {
                    // 리뷰에 해당하는 이미지들을 조회
                    List<BusinessReviewImages> reviewImages = businessReviewImagesRepository.findByReview(businessReview);

                    // BusinessReviewResponse 생성
                    BusinessReviewResponse businessReviewResponse = BusinessReviewResponse.builder()
                            .id(businessReview.getReviewId())
                            .businessId(businessReview.getBusiness().getBusiness_id())
                            .userId(businessReview.getUser().getUsers_id())
                            .rating(businessReview.getRating())
                            .businessReview(businessReview.getBusinessReview())
                            .imageUrls(reviewImages.stream().map(BusinessReviewImages::getImageUrl).collect(Collectors.toList()))
                            .xAddress(business.getX_address())
                            .yAddress(business.getY_address())
                            .build();

                    // 해당 날짜에 리뷰 추가 (여행 날짜를 기준으로 그룹화)
                    groupedReviews.computeIfAbsent(travelPlace.getDate(), k -> new ArrayList<>()).add(businessReviewResponse);
                }
            }
        }
        return groupedReviews;
    }

    // 여행 기록 수정
    @Transactional
    public TravelRecordResponse update(Long recordId, CreateTravelRecordRequest request, List<MultipartFile> newImages, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 여행 기록 조회
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        // 비즈니스 리뷰 처리
        Map<LocalDate, List<BusinessReviewResponse>> businessReviewResponses = handleBusinessReviews(record.getPlan(), user);

        // 여행 기록 수정 및 이미지 저장
        if (request.getOneLineReview() != null) {
            record.setRecordTitle(request.getOneLineReview());
        }
        if (request.getOverallReview() != null) {
            record.setRecordContent(request.getOverallReview());
        }
        record.setPublicPrivate(request.isPublicPrivate());

        // 기존 이미지 삭제 (S3 및 데이터베이스에서)
        deleteImagesFromRecord(record);

        // 새로운 이미지 저장
        List<String> imageUrls = saveImages(newImages, record);

        travelRecordsRepository.save(record);

        // 여행 플랜 정보 추가
        TravelPlans plan = record.getPlan();

        return new TravelRecordResponse(
                record.getRecordId(),
                record.getRecordTitle(),
                record.getRecordContent(),
                imageUrls,
                record.getPublicPrivate(),
                plan.getPlanId(),
                plan.getPlan_name(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getIsland() != null ? plan.getIsland().getName() : null,  // getName()으로 변경
                plan.getStatus(),
                record.getUser().getUsers_id(),
                businessReviewResponses,
                plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
        );
    }

    // 기존 이미지 삭제 메서드
    private void deleteImagesFromRecord(TravelRecords record) {
        // S3에서 이미지 삭제
        for (RecordImages image : record.getRecordImages()) {
            deleteImageFromS3(image.getImageUrl());
        }

        // 데이터베이스에서 이미지 삭제
        record.getRecordImages().clear();
        recordImagesRepository.deleteAllByRecord(record);  // 연관된 이미지를 모두 삭제
    }

    // 여행 기록 삭제
    @Transactional
    public void delete(Long recordId, Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        if (!record.getUser().equals(user)) {
            throw new IllegalArgumentException("You do not have permission to delete this record.");
        }

        // 이미지 삭제
        for (RecordImages image : record.getRecordImages()) {
            deleteImageFromS3(image.getImageUrl());
        }

        // 여행 기록 삭제
        travelRecordsRepository.delete(record);
    }

    // 이미지 삭제 메서드
    private void deleteImageFromS3(String imageUrl) {
        try {
            String key = extractKeyFromUrl(imageUrl);
            amazonS3Client.deleteObject(bucket, key);
        } catch (Exception e) {
            throw new ImageStorageException("Failed to delete image from S3", e);
        }
    }

    // 이미지 URL에서 키 추출
    private String extractKeyFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.indexOf(bucket) + bucket.length() + 1);
    }

    // 여행 기록 조회
    @Transactional
    public TravelRecordResponse getRecordById(Long recordId, Long userId) {
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        if (!record.getUser().getUsers_id().equals(userId)) {
            throw new IllegalArgumentException("You do not have permission to view this record.");
        }

        List<String> imageUrls = record.getRecordImages().stream()
                .map(RecordImages::getImageUrl)
                .collect(Collectors.toList());

        // 여행 플랜 정보 가져오기
        TravelPlans plan = record.getPlan();

        Double latitude = plan.getIsland() != null ? plan.getIsland().getLatitude() : null;
        Double longitude = plan.getIsland() != null ? plan.getIsland().getLongitude() : null;

        return new TravelRecordResponse(
                record.getRecordId(),
                record.getRecordTitle(),
                record.getRecordContent(),
                imageUrls,
                record.getPublicPrivate(),
                plan.getPlanId(),
                plan.getPlan_name(),
                plan.getStartDate(),
                plan.getEndDate(),
                plan.getIsland() != null ? plan.getIsland().getName() : null,  // getName()으로 수정
                plan.getStatus(),
                record.getUser().getUsers_id(),
                handleBusinessReviews(plan, record.getUser()),
                plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
        );
    }

    // 사용자별 여행 기록 조회
    @Transactional
    public List<TravelRecordResponse> getRecordsByUser() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<TravelRecords> records = travelRecordsRepository.findByUserWithImagesOrderByRecordIdDesc(user);

        return records.stream()
                .map(record -> {
                    TravelPlans plan = record.getPlan();
                    Map<LocalDate, List<BusinessReviewResponse>> businessReviewResponses = handleBusinessReviews(plan, user);
                    return new TravelRecordResponse(
                            record.getRecordId(),
                            record.getRecordTitle(),
                            record.getRecordContent(),
                            record.getRecordImages().stream()
                                    .map(RecordImages::getImageUrl)
                                    .collect(Collectors.toList()),
                            record.getPublicPrivate(),
                            plan.getPlanId(),
                            plan.getPlan_name(),
                            plan.getStartDate(),
                            plan.getEndDate(),
                            plan.getIsland() != null ? plan.getIsland().getName() : null,  // getName()으로 수정
                            plan.getStatus(),
                            record.getUser().getUsers_id(),
                            businessReviewResponses,
                            plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                            plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
                    );
                })
                .collect(Collectors.toList());
    }

    // 여행 계획별 여행 기록 조회
    @Transactional
    public List<TravelRecordResponse> getRecordsByPlan(Long planId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + planId));

        List<TravelRecords> records = travelRecordsRepository.findByPlanWithImagesOrderByRecordIdDesc(plan);

        return records.stream()
                .map(record -> {
                    Map<LocalDate, List<BusinessReviewResponse>> businessReviewResponses = handleBusinessReviews(plan, record.getUser());
                    return new TravelRecordResponse(
                            record.getRecordId(),
                            record.getRecordTitle(),
                            record.getRecordContent(),
                            record.getRecordImages().stream()
                                    .map(RecordImages::getImageUrl)
                                    .collect(Collectors.toList()),
                            record.getPublicPrivate(),
                            plan.getPlanId(),
                            plan.getPlan_name(),
                            plan.getStartDate(),
                            plan.getEndDate(),
                            plan.getIsland() != null ? plan.getIsland().getName() : null,  // getName()으로 수정
                            plan.getStatus(),
                            record.getUser().getUsers_id(),
                            businessReviewResponses,
                            plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                            plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
                    );
                })
                .collect(Collectors.toList());
    }

    // 사용자별 공개된 여행 기록 조회
    @Transactional
    public List<TravelRecordResponse> getRecordsByUserTrue() {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<TravelRecords> records = travelRecordsRepository.findByUserAndPublicPrivateOrderByRecordIdDesc(user, true);

        return records.stream()
                .map(record -> {
                    TravelPlans plan = record.getPlan();
                    Map<LocalDate, List<BusinessReviewResponse>> businessReviewResponses = handleBusinessReviews(plan, user);
                    return new TravelRecordResponse(
                            record.getRecordId(),
                            record.getRecordTitle(),
                            record.getRecordContent(),
                            record.getRecordImages().stream()
                                    .map(RecordImages::getImageUrl)
                                    .collect(Collectors.toList()),
                            record.getPublicPrivate(),
                            plan.getPlanId(),
                            plan.getPlan_name(),
                            plan.getStartDate(),
                            plan.getEndDate(),
                            plan.getIsland() != null ? plan.getIsland().getName() : null,  // getName()으로 수정
                            plan.getStatus(),
                            record.getUser().getUsers_id(),
                            businessReviewResponses,
                            plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                            plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
                    );
                })
                .collect(Collectors.toList());
    }

    // 여행 계획별 공개된 여행 기록 조회
    @Transactional
    public List<TravelRecordResponse> getRecordsByPlanTrue(Long planId) {
        Long userId = SecurityUtil.getAuthenticatedUserId();
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + planId));

        List<TravelRecords> records = travelRecordsRepository.findByPlanAndPublicPrivateOrderByRecordIdDesc(plan, true);

        return records.stream()
                .map(record -> {
                    Map<LocalDate, List<BusinessReviewResponse>> businessReviewResponses = handleBusinessReviews(plan, record.getUser());
                    return new TravelRecordResponse(
                            record.getRecordId(),
                            record.getRecordTitle(),
                            record.getRecordContent(),
                            record.getRecordImages().stream()
                                    .map(RecordImages::getImageUrl)
                                    .collect(Collectors.toList()),
                            record.getPublicPrivate(),
                            plan.getPlanId(),
                            plan.getPlan_name(),
                            plan.getStartDate(),
                            plan.getEndDate(),
                            plan.getIsland() != null ? plan.getIsland().getName() : null,  // getName()으로 수정
                            plan.getStatus(),
                            record.getUser().getUsers_id(),
                            businessReviewResponses,
                            plan.getIsland() != null ? plan.getIsland().getLatitude() : null,  // 위도 가져오기
                            plan.getIsland() != null ? plan.getIsland().getLongitude() : null  // 경도 가져오기
                    );
                })
                .collect(Collectors.toList());
    }

}
