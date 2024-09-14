package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.PostCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CommunityPosts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "users_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

    private String title;
    private String content;

    // 다수의 이미지 경로를 저장하는 필드
    @ElementCollection
    private List<String> imagePaths;  // 이미지 경로 리스트
}
