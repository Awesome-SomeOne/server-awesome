package com.example.SomeOne.service;

import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.Favorites;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.CommunityPostsRepository;
import com.example.SomeOne.repository.FavoritesRepository;
import com.example.SomeOne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final CommunityPostsRepository communityPostsRepository;
    private final UserRepository userRepository;

    @Autowired
    public FavoritesService(FavoritesRepository favoritesRepository, CommunityPostsRepository communityPostsRepository, UserRepository userRepository) {
        this.favoritesRepository = favoritesRepository;
        this.communityPostsRepository = communityPostsRepository;
        this.userRepository = userRepository;
    }

    public String favoritePost(Long postId, Long userId) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // userId로 사용자 조회
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이미 좋아요에 추가되었는지 확인
        Optional<Favorites> existingFavorite = favoritesRepository.findByPostAndUser(post, user);
        if (existingFavorite.isPresent()) {
            return "Already favorited";
        }

        // 좋아요 등록
        Favorites favorite = new Favorites();
        favorite.setPost(post);
        favorite.setUser(user);
        favoritesRepository.save(favorite);

        return "Favorited";
    }

    public Long getFavoriteCount(Long postId) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return favoritesRepository.countByPost(post);
    }

    // 좋아요 취소
    public String unfavoritePost(Long postId, Long userId) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 좋아요한 기록이 있는지 확인
        Optional<Favorites> existingFavorite = favoritesRepository.findByPostAndUser(post, user);
        if (existingFavorite.isPresent()) {
            // 좋아요 취소 (삭제)
            favoritesRepository.delete(existingFavorite.get());
            return "Favorite removed";
        } else {
            return "Favorite not found";
        }
    }
}
