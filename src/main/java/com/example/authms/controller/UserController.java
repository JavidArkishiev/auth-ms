package com.example.authms.controller;

import com.example.authms.dto.UserRequestDto;
import com.example.authms.dto.UserResponseDto;
import com.example.authms.entity.User;
import com.example.authms.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getAllUser() {
        return userService.getAllUser();
    }


    @GetMapping("get-user")
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public UserResponseDto getUser() {
        return userService.getUser();
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public UserRequestDto updateUser(@RequestParam Long userId, @RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(userId, userRequestDto);

    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUserById(@RequestParam Long userId) {
        userService.deleteUserById(userId);
        return "your account has been deleted";
    }
}
