package com.example.SomeOne.service;

import com.example.SomeOne.domain.Bookmarks;
import com.example.SomeOne.domain.CommunityPosts;
import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.BookmarksRepository;
import com.example.SomeOne.repository.CommunityPostsRepository;
import com.example.SomeOne.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookmarksService {

    private final BookmarksRepository bookmarksRepository;
    private final CommunityPostsRepository communityPostsRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookmarksService(BookmarksRepository bookmarksRepository, CommunityPostsRepository communityPostsRepository, UserRepository userRepository) {
        this.bookmarksRepository = bookmarksRepository;
        this.communityPostsRepository = communityPostsRepository;
        this.userRepository = userRepository;
    }

    // 북마크 추가
    public String bookmarkPost(Long postId, Long userId) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 이미 북마크 되어 있는지 확인
        Optional<Bookmarks> existingBookmark = bookmarksRepository.findByPostAndUser(post, user);
        if (existingBookmark.isPresent()) {
            return "Already bookmarked";
        }

        // 북마크 등록
        Bookmarks bookmark = new Bookmarks();
        bookmark.setPost(post);
        bookmark.setUser(user);
        bookmarksRepository.save(bookmark);

        return "Bookmarked";
    }

    // 북마크 취소
    public String unbookmarkPost(Long postId, Long userId) {
        CommunityPosts post = communityPostsRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 북마크 여부 확인
        Optional<Bookmarks> existingBookmark = bookmarksRepository.findByPostAndUser(post, user);
        if (existingBookmark.isPresent()) {
            bookmarksRepository.delete(existingBookmark.get());
            return "Bookmark removed";
        } else {
            return "Bookmark not found";
        }
    }
}
