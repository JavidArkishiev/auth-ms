package com.example.authms.mapper;

import com.example.authms.dto.request.SignUpRequest;
import com.example.authms.dto.request.UserRequestDto;
import com.example.authms.dto.response.UserResponseDto;
import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Mapper(componentModel = "spring")
public abstract class UserMapper {
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "otp", expression = "java(generateRandomOtp())")
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "password", expression = "java(passwordEncoder.encode(signUpRequest.getPassword()))")
    @Mapping(target = "roles", expression = "java(mapRole())")
    public abstract User mapToEntity(SignUpRequest signUpRequest);

    public List<Role> mapRole() {
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new UserNotFoundException("USER rolu tapılmadı"));
        List<Role> roles = new ArrayList<>();
        roles.add(role);
        return roles;
    }

    public String generateRandomOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public abstract List<UserResponseDto> mapToUserResponseDto(List<User> userList);

    public abstract UserResponseDto mapToUserDto(User userEntity);

    @Mapping(target = "email", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "otpGeneratedTime", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    @Mapping(target = "otp", ignore = true)
    @Mapping(target = "userId", ignore = true)
    public abstract User mapToUpdateUser(@MappingTarget User oldUser, UserRequestDto userRequestDto);
}
