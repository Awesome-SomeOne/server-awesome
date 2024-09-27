package com.example.SomeOne.service;

import com.example.SomeOne.domain.*;
import com.example.SomeOne.domain.enums.ReportReason;
import com.example.SomeOne.dto.Businesses.request.CreateBusinessReviewRequest;
import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import com.example.SomeOne.dto.TravelRecords.TravelDateImages;
import com.example.SomeOne.exception.ResourceNotFoundException;
import com.example.SomeOne.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessReviewsService {

    private final BusinessReviewsRepository businessReviewsRepository;
    private final BusinessReviewImagesRepository businessReviewImagesRepository;
    private final BusinessesRepository businessRepository;
    private final UserRepository userRepository;
    private final S3ImageUploadService s3ImageUploadService;
    private final TravelPlaceRepository travelPlaceRepository;

    // 비즈니스 리뷰 조회 (날짜별 그룹화)
    public Map<LocalDate, List<BusinessReviewResponse>> getBusinessReviews(Long businessId, Long userId) {
        // 비즈니스 찾기
        Businesses business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        // 유저 찾기
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // TravelPlace에서 비즈니스 여행 날짜 및 리뷰 찾기
        List<TravelPlace> travelPlaces = travelPlaceRepository.findByBusinessesAndTravelPlans_User(business, user);

        // 날짜별로 리뷰를 그룹화
        Map<LocalDate, List<BusinessReviewResponse>> groupedReviews = new HashMap<>();

        for (TravelPlace travelPlace : travelPlaces) {
            // BusinessReviews 조회 (유저와 비즈니스 기준으로 조회)
            Optional<BusinessReviews> reviewOptional = businessReviewsRepository.findByBusinessAndUser(business, user);

            // 리뷰가 있을 경우 처리
            if (reviewOptional.isPresent()) {
                BusinessReviews review = reviewOptional.get();

                // 리뷰 이미지 조회 (날짜 필터링 없이 리뷰 기준으로 조회)
                List<BusinessReviewImages> reviewImages = businessReviewImagesRepository.findByReview(review);

                // BusinessReviewResponse 생성
                BusinessReviewResponse reviewResponse = BusinessReviewResponse.builder()
                        .id(review.getReviewId())
                        .businessId(review.getBusiness().getBusiness_id())
                        .userId(review.getUser().getUsers_id())
                        .rating(review.getRating())
                        .businessReview(review.getBusinessReview())
                        .imageUrls(reviewImages.stream().map(BusinessReviewImages::getImageUrl).collect(Collectors.toList()))
                        .xAddress(business.getX_address())
                        .yAddress(business.getY_address())
                        .build();

                // 해당 날짜에 리뷰 추가
                groupedReviews.computeIfAbsent(travelPlace.getDate(), k -> new ArrayList<>()).add(reviewResponse);
            }
        }

        return groupedReviews;
    }

    @Transactional
    public BusinessReviewResponse createOrUpdateBusinessReview(Long userId, CreateBusinessReviewRequest request, List<MultipartFile> images) {
        Businesses business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + request.getBusinessId()));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        BusinessReviews review = businessReviewsRepository.findByBusinessAndUser(business, user)
                .map(existingReview -> {
                    existingReview.setRating(request.getRating());
                    existingReview.setBusinessReview(request.getBusinessReview());
                    return businessReviewsRepository.save(existingReview);
                })
                .orElseGet(() -> {
                    BusinessReviews newReview = BusinessReviews.builder()
                            .business(business)
                            .user(user)
                            .rating(request.getRating())
                            .businessReview(request.getBusinessReview())
                            .build();
                    return businessReviewsRepository.save(newReview);
                });

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = s3ImageUploadService.saveImage(image);
            imageUrls.add(imageUrl);
            BusinessReviewImages reviewImage = new BusinessReviewImages(review, imageUrl);
            businessReviewImagesRepository.save(reviewImage);
        }

        return BusinessReviewResponse.builder()
                .id(review.getReviewId())
                .businessId(review.getBusiness().getBusiness_id())
                .userId(review.getUser().getUsers_id())
                .rating(review.getRating())
                .businessReview(review.getBusinessReview())
                .imageUrls(imageUrls)
                .xAddress(business.getX_address())
                .yAddress(business.getY_address())
                .build();
    }

    @Transactional
    public void deleteBusinessReview(Long businessId, Long userId) {
        Businesses business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        BusinessReviews review = businessReviewsRepository.findByBusinessAndUser(business, user)
                .orElseThrow(() -> new ResourceNotFoundException("Business review not found for businessId: " + businessId + " and userId: " + userId));

        // 삭제 전 리뷰와 연결된 이미지 삭제
        List<BusinessReviewImages> reviewImages = businessReviewImagesRepository.findByReview(review);
        businessReviewImagesRepository.deleteAll(reviewImages);

        businessReviewsRepository.delete(review);
    }

    public List<BusinessReviewResponse> getAllBusinessReviews(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // 신고되지 않은 리뷰만 필터링
        List<BusinessReviews> reviews = businessReviewsRepository.findByUser(user)
                .stream()
                .filter(review -> !review.getIsReported())  // 신고되지 않은 리뷰만 필터링
                .collect(Collectors.toList());

        return reviews.stream()
                .map(review -> BusinessReviewResponse.builder()
                        .id(review.getReviewId())
                        .businessId(review.getBusiness().getBusiness_id())
                        .userId(review.getUser().getUsers_id())
                        .rating(review.getRating())
                        .businessReview(review.getBusinessReview())
                        .imageUrls(businessReviewImagesRepository.findByReview(review)
                                .stream()
                                .map(BusinessReviewImages::getImageUrl)
                                .collect(Collectors.toList()))
                        .xAddress(review.getBusiness().getX_address())  // 조회 시 좌표 포함
                        .yAddress(review.getBusiness().getY_address())  // 조회 시 좌표 포함
                        .build())
                .collect(Collectors.toList());
    }

    // 리뷰 신고 처리
    @Transactional
    public boolean reportReview(Long reviewId, Long userId, ReportReason reportReason) {
        BusinessReviews review = businessReviewsRepository.findById(reviewId)
                .orElse(null);

        if (review == null) {
            return false; // 리뷰가 없을 경우 false 반환
        }

        // 리뷰 신고 처리
        review.report(reportReason); // 신고 사유를 함께 처리
        review.hideRecordDueToReport();
        businessReviewsRepository.save(review);
        return true; // 신고 성공 시 true 반환
    }
}
