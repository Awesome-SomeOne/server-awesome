package com.example.SomeOne.service;

import com.example.SomeOne.domain.Users;
import com.example.SomeOne.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public Users findById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException());
    }
}
