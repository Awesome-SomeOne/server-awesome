package com.example.SomeOne.service;

import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.repository.CommunityPostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommunityPostsService {

    private final CommunityPostsRepository communityPostsRepository;
    private final S3ImageUploadService s3ImageUploadService;

    @Autowired
    public CommunityPostsService(CommunityPostsRepository communityPostsRepository, S3ImageUploadService s3ImageUploadService) {
        this.communityPostsRepository = communityPostsRepository;
        this.s3ImageUploadService = s3ImageUploadService;
    }

    // 게시물 생성
    public CommunityPosts createPost(CommunityPosts post, List<MultipartFile> images) {
        // 업로드된 이미지 URL을 저장할 리스트
        List<String> imageUrls = new ArrayList<>();

        // 이미지가 있으면 S3에 업로드 후 URL 저장
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrl = s3ImageUploadService.saveImage(image);  // 이미지 업로드 후 URL 가져오기
                imageUrls.add(imageUrl);
            }
            post.setImagePaths(imageUrls); // 이미지 URL 리스트를 엔티티에 저장
        }
        return communityPostsRepository.save(post);
    }

    // 게시물 조회
    public Optional<CommunityPosts> getPostById(Long id) {
        return communityPostsRepository.findById(id);
    }

    // 모든 게시물 조회
    public List<CommunityPosts> getAllPosts() {
        return communityPostsRepository.findAll();
    }

    // 게시물 삭제
    public void deletePost(Long id) {
        communityPostsRepository.deleteById(id);
    }

    // 게시물 수정
    public CommunityPosts updatePost(CommunityPosts post, List<MultipartFile> newImages) {
        // 새로운 이미지가 있는 경우 처리
        if (newImages != null && !newImages.isEmpty()) {
            List<String> newImageUrls = new ArrayList<>();
            for (MultipartFile image : newImages) {
                String imageUrl = s3ImageUploadService.saveImage(image);  // 이미지 업로드
                newImageUrls.add(imageUrl); // URL을 리스트에 추가
            }
            post.setImagePaths(newImageUrls); // 게시물에 이미지 URL 리스트 업데이트
        }
        return communityPostsRepository.save(post);
    }
}
