package com.example.SomeOne.repository;

import com.example.SomeOne.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Users, Long> {
}
