package com.example.authms.service;

import com.example.authms.dto.UserRequestDto;
import com.example.authms.dto.UserResponseDto;
import com.example.authms.entity.User;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.mapper.UserMapper;
import com.example.authms.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private JWTService jwtService;

    @Autowired
    public void setJwtService(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }


    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));
    }

    public List<UserResponseDto> getAllUser() {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new UserNotFoundException("istifadəçi tapılmadı");
        }
        return userMapper.mapToUserResponseDto(userList);

    }

    public UserResponseDto getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.mapToUserDto(user);
    }

    public UserRequestDto updateUser(Long userId, UserRequestDto userRequestDto) {
        User oldUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));
        if (oldUser != null) {
            User updateUser = userMapper.mapToUpdateUser(oldUser, userRequestDto);
            userRepository.save(updateUser);
        }
        return userRequestDto;

    }

    public void deleteUserById(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));
        userRepository.delete(userEntity);
    }


}
