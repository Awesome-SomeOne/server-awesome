package com.example.SomeOne.service;

import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findById(Long.parseLong(username)) // users_id로 사용자 조회
                .orElseThrow(() -> new UsernameNotFoundException("User not found with users_id: " + username));

        String password = user.getPassword() != null ? user.getPassword() : "";  // null 값 방지

        return new org.springframework.security.core.userdetails.User(
                String.valueOf(user.getUsers_id()), // users_id를 username 대신 사용
                password,
                Collections.emptyList()  // 권한 리스트는 비어있는 상태로 설정
        );
    }

}
