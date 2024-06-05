package com.example.authms.repository;

import com.example.authms.dto.request.OtpDto;
import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {



    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByOtp(String otp);


    @Query("SELECT r FROM Role r JOIN r.users u WHERE u.id = :userId")
    List<Role> findAllByUserId(Long userId);
}
