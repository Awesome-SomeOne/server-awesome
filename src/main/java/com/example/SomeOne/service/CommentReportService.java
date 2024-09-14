package com.example.SomeOne.service;

import com.example.SomeOne.domain.CommentReport;
import com.example.SomeOne.domain.Comments;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.CommentReportRepository;
import com.example.SomeOne.repository.CommentRepository;
import com.example.SomeOne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentReportService {

    private final CommentReportRepository commentReportRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentReportService(CommentReportRepository commentReportRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.commentReportRepository = commentReportRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public String reportComment(Long commentId, Long userId, String reason, String details) {
        Comments comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        CommentReport report = new CommentReport();
        report.setComment(comment);
        report.setUser(user);
        report.setReason(reason);
        report.setDetails(details);

        commentReportRepository.save(report);

        return "댓글 신고가 성공적으로 접수되었습니다.";
    }
}
