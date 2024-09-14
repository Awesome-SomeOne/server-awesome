package com.example.SomeOne.repository;

import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.CommunityReport;
import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {
    List<CommunityReport> findByPostAndUser(CommunityPosts post, Users user);  // 게시물과 사용자로 신고 검색
}
