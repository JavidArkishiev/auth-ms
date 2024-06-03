package com.example.authms.service;

import com.example.authms.dto.UserRequestDto;
import com.example.authms.dto.UserResponseDto;
import com.example.authms.entity.User;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.mapper.UserMapper;
import com.example.authms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;

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

    public UserResponseDto getUserById(Long userId) {
        User userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));
        return userMapper.mapToUserDto(userEntity);

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
