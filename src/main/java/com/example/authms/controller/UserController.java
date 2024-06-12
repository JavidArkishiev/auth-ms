package com.example.authms.controller;

import com.example.authms.dto.request.ChangePasswordRequest;
import com.example.authms.dto.request.DeletePasswordDto;
import com.example.authms.dto.request.UserRequestDto;
import com.example.authms.dto.response.UserResponseDto;
import com.example.authms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public UserRequestDto updateUser(@RequestBody UserRequestDto userRequestDto) {
        return userService.updateUser(userRequestDto);

    }

    @DeleteMapping()
    @PreAuthorize("hasAuthority('USER')")
    @ResponseStatus(HttpStatus.OK)
    public String deleteUser(@Valid @RequestBody DeletePasswordDto passwordDto) {
        userService.deleteUser(passwordDto);
        return "Hesabınız uğurla silindi";
    }

    @PutMapping("change-password")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest request,
                                                 Principal principal) {
        userService.changePassword(principal, request);
        return ResponseEntity.ok("Success");

    }
}
