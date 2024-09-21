package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    // ID로 사용자 검색
    Optional<Users> findById(Long users_id);

    // username으로 사용자 검색
    Optional<Users> findByUsername(String username);

    // userId로 사용자 검색
    Optional<Users> findByUserId(String users_id);

    // 카카오 사용자 ID로 조회
    Optional<Users> findByKakaoUserId(String kakaoUserId);

    // 소셜 로그인 여부로 사용자 검색
    //Optional<Users> findBySocialLogin(Boolean socialLogin);
}
