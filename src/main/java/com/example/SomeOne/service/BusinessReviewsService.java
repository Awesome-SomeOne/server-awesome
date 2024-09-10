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
                .shortReview(review.getShortReview())
                .detailedReview(review.getDetailedReview())
                .imageUrls(imageUrls)
                .build();
    }

    @Transactional
    public BusinessReviewResponse createOrUpdateBusinessReview(CreateBusinessReviewRequest request, List<MultipartFile> images) {
        Businesses business = businessRepository.findById(request.getBusinessId())
                .orElseThrow(() -> new ResourceNotFoundException("Business not found with id: " + request.getBusinessId()));

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        BusinessReviews review = businessReviewsRepository.findByBusinessAndUser(business, user)
                .map(existingReview -> {
                    existingReview.setRating(request.getRating());
                    existingReview.setShortReview(request.getShortReview());
                    existingReview.setDetailedReview(request.getDetailedReview());
                    return businessReviewsRepository.save(existingReview);
                })
                .orElseGet(() -> {
                    BusinessReviews newReview = BusinessReviews.builder()
                            .business(business)
                            .user(user)
                            .rating(request.getRating())
                            .shortReview(request.getShortReview())
                            .detailedReview(request.getDetailedReview())
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
                .shortReview(review.getShortReview())
                .detailedReview(review.getDetailedReview())
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

        // 삭제 전에 리뷰와 연결된 이미지를 삭제합니다.
        List<BusinessReviewImages> reviewImages = businessReviewImagesRepository.findByReview(review);
        businessReviewImagesRepository.deleteAll(reviewImages);

        businessReviewsRepository.delete(review);
    }
}