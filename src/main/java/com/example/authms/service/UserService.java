package com.example.authms.service;

import com.example.authms.dto.request.ChangePasswordRequest;
import com.example.authms.dto.request.DeletePasswordDto;
import com.example.authms.dto.request.UserRequestDto;
import com.example.authms.dto.response.UserResponseDto;
import com.example.authms.entity.User;
import com.example.authms.exception.AllException;
import com.example.authms.mapper.UserMapper;
import com.example.authms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private UserRepository userRepository;
    private UserMapper userMapper;
    private JWTService jwtService;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
                .orElseThrow(() -> new AllException("istifadəçi tapılmadı"));
    }

    public List<UserResponseDto> getAllUser() {
        List<User> userList = userRepository.findAll();
        if (userList.isEmpty()) {
            throw new AllException("istifadəçi tapılmadı");
        }
        return userMapper.mapToUserResponseDto(userList);

    }

    public UserResponseDto getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new AllException("istifadəçi tapılmadı"));
        return userMapper.mapToUserDto(user);
    }

    public UserRequestDto updateUser(UserRequestDto userRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User oldUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new AllException("istifadəçi tapılmadı"));
        if (oldUser != null) {
            User updateUser = userMapper.mapToUpdateUser(oldUser, userRequestDto);
            userRepository.save(updateUser);
        }
        return userRequestDto;

    }

    public void deleteUser(DeletePasswordDto passwordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User userEntity = userRepository.findByEmail(username)
                .orElseThrow(() -> new AllException("istifadəçi tapılmadı"));
        if (!passwordEncoder.matches(passwordDto.getPassword(), userEntity.getPassword())) {
            throw new AllException("istifəçi şifrəniz doğru deyil");
        }
        userRepository.delete(userEntity);
    }


    public void changePassword(Principal principal, ChangePasswordRequest request) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AllException("cari şifrə doğru deyil");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new AllException("hər iki şifrə eyni olmalıdır");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
//
    }
}
