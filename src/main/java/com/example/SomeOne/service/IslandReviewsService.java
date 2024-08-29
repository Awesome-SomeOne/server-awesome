package com.example.SomeOne.service;

import com.example.SomeOne.domain.Island;
import com.example.SomeOne.domain.IslandReviews;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.dto.TravelRecords.Request.CreateIslandReviewRequest;
import com.example.SomeOne.exception.ResourceNotFoundException;
import com.example.SomeOne.repository.IslandReviewsRepository;
import com.example.SomeOne.repository.IslandRepository;
import com.example.SomeOne.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IslandReviewsService {
    private final IslandReviewsRepository islandReviewsRepository;
    private final IslandRepository islandRepository;
    private final UserRepository userRepository;

    public IslandReviews getIslandReview(Long islandId, Long userId) {
        Island island = islandRepository.findById(islandId)
                .orElseThrow(() -> new ResourceNotFoundException("Island not found with id: " + islandId));

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return islandReviewsRepository.findFirstByIslandAndUser(island, user)
                .orElseThrow(() -> new ResourceNotFoundException("Island review not found for islandId: " + islandId + " and userId: " + userId));
    }

    @Transactional
    public IslandReviews createOrUpdateIslandReview(CreateIslandReviewRequest request) {
        Island island = islandRepository.findById(request.getIslandId())
                .orElseThrow(() -> new ResourceNotFoundException("Island not found with id: " + request.getIslandId()));

        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        return islandReviewsRepository.findFirstByIslandAndUser(island, user)
                .map(existingReview -> {
                    existingReview.setRating(request.getRating());
                    existingReview.setShortReview(request.getShortReview());
                    existingReview.setDetailedReview(request.getDetailedReview());
                    return islandReviewsRepository.save(existingReview);
                })
                .orElseGet(() -> {
                    IslandReviews newReview = IslandReviews.builder()
                            .island(island)
                            .user(user)
                            .rating(request.getRating())
                            .shortReview(request.getShortReview())
                            .detailedReview(request.getDetailedReview())
                            .build();
                    return islandReviewsRepository.save(newReview);
                });
    }

    @Transactional
    public void deleteIslandReview(Long reviewId) {
        IslandReviews review = islandReviewsRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Island review not found with id: " + reviewId));
        islandReviewsRepository.delete(review);
    }
}
