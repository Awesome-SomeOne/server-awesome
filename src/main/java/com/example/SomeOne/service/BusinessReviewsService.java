package com.example.SomeOne.service;

import com.example.SomeOne.domain.BusinessReviewImages;
import com.example.SomeOne.domain.BusinessReviews;
import com.example.SomeOne.domain.Businesses;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.Businesses.request.CreateBusinessReviewRequest;
import com.example.SomeOne.dto.Businesses.response.BusinessReviewResponse;
import com.example.SomeOne.exception.ResourceNotFoundException;
import com.example.SomeOne.repository.BusinessReviewImagesRepository;
import com.example.SomeOne.repository.BusinessReviewsRepository;
import com.example.SomeOne.repository.BusinessesRepository;
import com.example.SomeOne.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BusinessReviewsService {

    private final BusinessReviewsRepository businessReviewsRepository;
    private final BusinessReviewImagesRepository businessReviewImagesRepository;
    private final BusinessesRepository businessRepository;
    private final UserRepository userRepository;
    private final S3ImageUploadService s3ImageUploadService;

    public BusinessReviewResponse getBusinessReview(Long businessId, Long userId) {
        Businesses business = businessRepository.findById(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + businessId));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        BusinessReviews review = businessReviewsRepository.findByBusinessAndUser(business, user)
                .orElseThrow(() -> new ResourceNotFoundException("Business review not found for businessId: " + businessId + " and userId: " + userId));

        List<String> imageUrls = businessReviewImagesRepository.findByReview(review)
                .stream()
                .map(BusinessReviewImages::getImageUrl)
                .collect(Collectors.toList());

        return BusinessReviewResponse.builder()
                .id(review.getReviewId())
                .businessId(review.getBusiness().getBusiness_id())
                .userId(review.getUser().getUsers_id())
                .rating(review.getRating())
                .businessReview(review.getBusinessReview())
                .imageUrls(imageUrls)
                .build();
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
                .map(review -> new BusinessReviewResponse(
                        review.getReviewId(),
                        review.getBusiness().getBusiness_id(),
                        review.getUser().getUsers_id(),
                        review.getRating(),
                        review.getBusinessReview(),
                        businessReviewImagesRepository.findByReview(review)
                                .stream()
                                .map(BusinessReviewImages::getImageUrl)
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean reportReview(Long reviewId, Long userId) {
        BusinessReviews review = businessReviewsRepository.findById(reviewId)
                .orElse(null);

        if (review == null) {
            return false; // 리뷰가 없을 경우 false 반환
        }

        // 리뷰를 신고 처리하고 비공개로 설정
        review.hideRecordDueToReport();
        businessReviewsRepository.save(review);
        return true; // 신고 성공 시 true 반환
    }
}
