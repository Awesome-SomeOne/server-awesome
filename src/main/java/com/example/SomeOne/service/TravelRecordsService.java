package com.example.SomeOne.service;

import com.example.SomeOne.domain.*;
import com.example.SomeOne.dto.TravelRecords.Request.CreateTravelRecordRequest;
import com.example.SomeOne.dto.TravelRecords.Response.TravelRecordResponse;
import com.example.SomeOne.dto.TravelRecords.Response.IslandReviewResponse;
import com.example.SomeOne.exception.ImageStorageException;
import com.example.SomeOne.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TravelRecordsService {

    private final TravelRecordsRepository travelRecordsRepository;
    private final IslandReviewsRepository islandReviewsRepository;
    private final IslandRepository islandRepository;
    private final TravelPlansRepository travelPlansRepository;
    private final UserRepository userRepository;

    private final String imageUploadDir = "D:/SomeOne/static/images";

    @Transactional
    public TravelRecordResponse create(List<MultipartFile> images, CreateTravelRecordRequest request) {
        // 여행 계획 조회
        TravelPlans plan = travelPlansRepository.findById(request.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + request.getPlanId()));

        // 사용자 조회
        Users user = plan.getUser();

        // 여행 기록 생성
        TravelRecords record = TravelRecords.builder()
                .user(user)
                .plan(plan)
                .recordTitle(request.getOneLineReview())
                .recordContent(request.getOverallReview())
                .publicPrivate(request.isPublicPrivate())
                .build();

        // 이미지 저장 및 URL 생성
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = saveImage(image);
            imageUrls.add(imageUrl);

            RecordImages recordImage = RecordImages.builder()
                    .image_url(imageUrl)
                    .build();

            record.addRecordImage(recordImage);
        }

        // 여행 기록 저장
        TravelRecords savedRecord = travelRecordsRepository.save(record);

        // 섬 리뷰 정보 초기화
        IslandReviewResponse islandReviewResponse = null;

        // 섬 ID가 제공되었을 경우 섬 리뷰 생성
        if (request.getIslandId() != null) {
            Island island = islandRepository.findById(request.getIslandId())
                    .orElseThrow(() -> new IllegalArgumentException("Island not found with id: " + request.getIslandId()));

            IslandReviews islandReview = IslandReviews.builder()
                    .island(island)
                    .user(user)
                    .rating(request.getRating())
                    .shortReview(request.getShortReview())
                    .detailedReview(request.getDetailedReview())
                    .build();

            islandReview.setTravelRecord(savedRecord);
            IslandReviews savedIslandReview = islandReviewsRepository.save(islandReview);
            islandReviewResponse = new IslandReviewResponse(savedIslandReview);
        }

        // TravelRecordResponse 객체 생성 및 반환
        return new TravelRecordResponse(savedRecord, imageUrls.isEmpty() ? null : imageUrls.get(0), islandReviewResponse);
    }

    private String saveImage(MultipartFile image) {
        try {
            // 파일 경로 설정
            String filePath = imageUploadDir + "/" + image.getOriginalFilename();
            File dest = new File(filePath);
            // 파일 저장
            image.transferTo(dest);
            // URL 반환
            return "http://localhost:8080/images/" + image.getOriginalFilename();
        } catch (IOException e) {
            throw new ImageStorageException("Failed to store file", e);
        }
    }

    @Transactional
    public TravelRecordResponse update(Long recordId, CreateTravelRecordRequest request, List<MultipartFile> newImages) {
        // 여행 기록 조회
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        // 여행 기록 수정
        record.setRecordTitle(request.getOneLineReview());
        record.setRecordContent(request.getOverallReview());
        record.setPublicPrivate(request.isPublicPrivate());

        // 기존 이미지 제거
        record.getRecordImages().clear();
        // 새 이미지 저장
        if (newImages != null && !newImages.isEmpty()) {
            for (MultipartFile image : newImages) {
                String imageUrl = saveImage(image);
                RecordImages recordImage = RecordImages.builder()
                        .image_url(imageUrl)
                        .build();
                record.addRecordImage(recordImage);
            }
        }

        // 여행 기록 저장
        TravelRecords updatedRecord = travelRecordsRepository.save(record);

        // 섬 리뷰 정보 초기화
        IslandReviewResponse islandReviewResponse = null;

        // 섬 ID가 제공되었을 경우 섬 리뷰 생성
        if (request.getIslandId() != null) {
            Island island = islandRepository.findById(request.getIslandId())
                    .orElseThrow(() -> new IllegalArgumentException("Island not found with id: " + request.getIslandId()));

            IslandReviews islandReview = IslandReviews.builder()
                    .island(island)
                    .user(record.getUser())
                    .rating(request.getRating())
                    .shortReview(request.getShortReview())
                    .detailedReview(request.getDetailedReview())
                    .build();

            islandReview.setTravelRecord(updatedRecord);
            IslandReviews savedIslandReview = islandReviewsRepository.save(islandReview);
            islandReviewResponse = new IslandReviewResponse(savedIslandReview);
        }

        // TravelRecordResponse 객체 생성 및 반환
        return new TravelRecordResponse(updatedRecord, updatedRecord.getRecordImages().isEmpty() ? null : updatedRecord.getRecordImages().get(0).getImage_url(), islandReviewResponse);
    }

    @Transactional
    public void delete(Long recordId) {
        // 여행 기록 조회
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        // 여행 기록의 모든 이미지 삭제
        for (RecordImages image : record.getRecordImages()) {
            deleteImageFromStorage(image.getImage_url());
        }

        // 여행 기록 삭제
        travelRecordsRepository.delete(record);
    }

    private void deleteImageFromStorage(String imageUrl) {
        File file = new File(imageUploadDir + "/" + imageUrl.substring(imageUrl.lastIndexOf('/') + 1));
        if (file.exists()) {
            file.delete();
        }
    }

    // 여행 기록 공유
    public TravelRecordResponse getRecordById(Long recordId) {
        // 여행 기록 조회
        TravelRecords record = travelRecordsRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("Travel record not found with id: " + recordId));

        // 이미지 URL 목록 생성
        List<String> imageUrls = record.getRecordImages().stream()
                .map(RecordImages::getImage_url)
                .collect(Collectors.toList());

        // 섬 리뷰 정보 초기화
        IslandReviewResponse islandReviewResponse = null;

        // 섬 리뷰 정보가 있으면 설정
        if (record.getIslandReview() != null) {
            islandReviewResponse = new IslandReviewResponse(record.getIslandReview());
        }

        // TravelRecordResponse 객체 생성 및 반환
        return new TravelRecordResponse(record, imageUrls.isEmpty() ? null : imageUrls.get(0), islandReviewResponse);
    }

    public List<TravelRecordResponse> getRecordsByUser(Long userId) {
        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 사용자별 여행 기록 조회
        List<TravelRecords> records = travelRecordsRepository.findByUserWithImagesOrderByRecordIdDesc(user);

        // TravelRecordResponse 객체 목록 생성
        return records.stream()
                .map(record -> {
                    IslandReviewResponse islandReviewResponse = null;

                    // 섬 리뷰 정보가 있으면 설정
                    if (record.getIslandReview() != null) {
                        islandReviewResponse = new IslandReviewResponse(record.getIslandReview());
                    }

                    return new TravelRecordResponse(record,
                            record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url(),
                            islandReviewResponse);
                })
                .collect(Collectors.toList());
    }

    public List<TravelRecordResponse> getRecordsByPlan(Long planId) {
        // 여행 계획 조회
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + planId));

        // 여행 계획별 여행 기록 조회
        List<TravelRecords> records = travelRecordsRepository.findByPlanWithImagesOrderByRecordIdDesc(plan);

        // TravelRecordResponse 객체 목록 생성
        return records.stream()
                .map(record -> {
                    IslandReviewResponse islandReviewResponse = null;

                    // 섬 리뷰 정보가 있으면 설정
                    if (record.getIslandReview() != null) {
                        islandReviewResponse = new IslandReviewResponse(record.getIslandReview());
                    }

                    return new TravelRecordResponse(record,
                            record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url(),
                            islandReviewResponse);
                })
                .collect(Collectors.toList());
    }

    public List<TravelRecordResponse> getRecordsByUserTrue(Long userId) {
        // 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // 사용자별 공개된 여행 기록 조회
        List<TravelRecords> records = travelRecordsRepository.findByUserAndPublicPrivateOrderByRecordIdDesc(user, true);

        // TravelRecordResponse 객체 목록 생성
        return records.stream()
                .map(record -> {
                    IslandReviewResponse islandReviewResponse = null;

                    // 섬 리뷰 정보가 있으면 설정
                    if (record.getIslandReview() != null) {
                        islandReviewResponse = new IslandReviewResponse(record.getIslandReview());
                    }

                    return new TravelRecordResponse(record,
                            record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url(),
                            islandReviewResponse);
                })
                .collect(Collectors.toList());
    }

    public List<TravelRecordResponse> getRecordsByPlanTrue(Long planId) {
        // 여행 계획 조회
        TravelPlans plan = travelPlansRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Travel plan not found with id: " + planId));

        // 여행 계획별 공개된 여행 기록 조회
        List<TravelRecords> records = travelRecordsRepository.findByPlanAndPublicPrivateOrderByRecordIdDesc(plan, true);

        // TravelRecordResponse 객체 목록 생성
        return records.stream()
                .map(record -> {
                    IslandReviewResponse islandReviewResponse = null;

                    // 섬 리뷰 정보가 있으면 설정
                    if (record.getIslandReview() != null) {
                        islandReviewResponse = new IslandReviewResponse(record.getIslandReview());
                    }

                    return new TravelRecordResponse(record,
                            record.getRecordImages().isEmpty() ? null : record.getRecordImages().get(0).getImage_url(),
                            islandReviewResponse);
                })
                .collect(Collectors.toList());
    }
}
