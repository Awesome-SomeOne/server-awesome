package com.example.SomeOne.controller;

import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.domain.enums.PostCategory;
import com.example.SomeOne.dto.Bookmark.request.BookmarkRequest;
import com.example.SomeOne.dto.Bookmark.response.BookmarkResponse;
import com.example.SomeOne.dto.Favorites.request.FavoritesRequest;
import com.example.SomeOne.dto.community.request.CommunityPostRequest;
import com.example.SomeOne.dto.community.request.CommunityReportRequest;
import com.example.SomeOne.dto.community.response.CommunityPostResponse;
import com.example.SomeOne.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/api/posts")
public class CommunityPostsController {

    private final CommunityPostsService communityPostsService;
    private final UserService userService;
    private final FavoritesService favoritesService;
    private final BookmarksService bookmarksService;
    private final CommunityReportService communityReportService;


    @Autowired
    public CommunityPostsController(CommunityPostsService communityPostsService, UserService userService, FavoritesService favoritesService, BookmarksService bookmarksService, CommunityReportService communityReportService) {
        this.communityPostsService = communityPostsService;
        this.userService = userService;
        this.favoritesService = favoritesService;
        this.bookmarksService = bookmarksService;
        this.communityReportService = communityReportService;
    }

    // 게시물 생성
    @PostMapping
    public ResponseEntity<CommunityPostResponse> createPost(
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam Long userId,
            @ModelAttribute CommunityPostRequest postRequest) {

        // 사용자 조회
        Users user = userService.findById(userId);
        CommunityPosts post = mapToCommunityPost(postRequest);
        post.setUser(user);  // 게시물에 사용자 설정

        // 이미지가 없을 때 처리
        if (images == null || images.isEmpty()) {
            System.out.println("이미지 없이 게시물을 생성합니다.");
        }

        // 이미지가 있는 경우 처리
        CommunityPosts createdPost = communityPostsService.createPost(post, images);
        List<String> imageUrls = createdPost.getImagePaths();  // 이미지 경로 리스트 반환

        // 응답에 userId 포함
        CommunityPostResponse response = new CommunityPostResponse("Success", createdPost.getPostId(), imageUrls, user.getUsers_id());

        return ResponseEntity.ok(response);
    }

    // 게시물 수정
    @PatchMapping("/{id}")
    public ResponseEntity<CommunityPostResponse> updatePost(
            @PathVariable Long id,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam Long userId,
            @ModelAttribute CommunityPostRequest postRequest) {

        CommunityPosts post = communityPostsService.getPostById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // 게시물 수정 로직
        Users user = userService.findById(userId);
        post.setUser(user);
        updateCommunityPost(post, postRequest);

        // 이미지가 없는 경우 처리
        if (images == null || images.isEmpty()) {
            System.out.println("이미지 없이 게시물을 수정합니다.");
        }

        // 이미지가 있는 경우 처리
        CommunityPosts updatedPost = communityPostsService.updatePost(post, images);
        List<String> imageUrls = updatedPost.getImagePaths();  // 이미지 경로 리스트 반환

        // 응답에 userId 포함
        CommunityPostResponse response = new CommunityPostResponse("Success", updatedPost.getPostId(), imageUrls, user.getUsers_id());

        return ResponseEntity.ok(response);
    }

    // 게시물 생성/수정 시 공통 로직을 메서드로 분리
    private CommunityPosts mapToCommunityPost(CommunityPostRequest request) {
        CommunityPosts post = new CommunityPosts();
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        PostCategory category;
        try {
            category = PostCategory.valueOf(request.getCategory());
        } catch (IllegalArgumentException e) {
            category = PostCategory.DEFAULT;
        }
        post.setCategory(category);
        return post;
    }

    private void updateCommunityPost(CommunityPosts post, CommunityPostRequest request) {
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        PostCategory category;
        try {
            category = PostCategory.valueOf(request.getCategory());
        } catch (IllegalArgumentException e) {
            category = PostCategory.DEFAULT;
        }
        post.setCategory(category);
    }

    // 좋아요 버튼 누름
    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @RequestBody FavoritesRequest likeRequest) {
        // 클래스 이름(FavoritesService) 대신 인스턴스(favoritesService)를 사용
        String result = favoritesService.favoritePost(postId, likeRequest.getUserId());
        return ResponseEntity.ok(result);
    }

    // 좋아요 버튼 취소
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId, @RequestBody FavoritesRequest likeRequest) {
        String result = favoritesService.unfavoritePost(postId, likeRequest.getUserId());
        return ResponseEntity.ok(result);
    }

    // 스크랩(북마크) 추가
    @PostMapping("/{postId}/scrap")
    public ResponseEntity<BookmarkResponse> bookmarkPost(@PathVariable Long postId, @RequestBody BookmarkRequest bookmarkRequest) {
        String result = bookmarksService.bookmarkPost(postId, bookmarkRequest.getUserId());
        return ResponseEntity.ok(new BookmarkResponse("success", result));
    }

    // 스크랩(북마크) 취소
    @DeleteMapping("/{postId}/scrap")
    public ResponseEntity<BookmarkResponse> unbookmarkPost(@PathVariable Long postId, @RequestBody BookmarkRequest bookmarkRequest) {
        String result = bookmarksService.unbookmarkPost(postId, bookmarkRequest.getUserId());
        return ResponseEntity.ok(new BookmarkResponse("success", result));
    }

    // 게시물 신고하기
    @PostMapping("/{postId}/report")
    public ResponseEntity<CommunityPostResponse> reportPost(
            @PathVariable Long postId,
            @RequestBody CommunityReportRequest reportRequest,
            @RequestParam Long userId) {

        String result = communityReportService.reportPost(postId, userId, reportRequest.getReason(), reportRequest.getDetails());
        return ResponseEntity.ok(new CommunityPostResponse("success",  postId, Collections.emptyList(), userId));
    }

}

