package com.example.SomeOne.domain;

import com.example.SomeOne.domain.enums.PostCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class CommunityPosts {

    @Id @GeneratedValue
    private Long post_id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "users_id")
    private Users user;

    private PostCategory category;
    private String title;
    private String content;
}
