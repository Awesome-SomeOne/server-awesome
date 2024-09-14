package com.example.SomeOne.service;

import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.CommunityReport;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.CommunityPostsRepository;
import com.example.SomeOne.repository.CommunityReportRepository;
import com.example.SomeOne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommunityReportService {

    private final CommunityReportRepository communityReportRepository;
    private final CommunityPostsRepository communityPostsRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommunityReportService(CommunityReportRepository communityReportRepository, CommunityPostsRepository communityPostsRepository, UserRepository userRepository) {
        this.communityReportRepository = communityReportRepository;
        this.communityPostsRepository = communityPostsRepository;
        this.userRepository = userRepository;
    }

    // 게시물 신고하기
    public String reportPost(Long postId, Long userId, String reason, String details) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 신고 등록
        CommunityReport report = new CommunityReport();
        report.setPost(post);
        report.setUser(user);
        report.setReason(reason);
        report.setDetails(details);

        communityReportRepository.save(report);

        return "Reported";
    }
}
