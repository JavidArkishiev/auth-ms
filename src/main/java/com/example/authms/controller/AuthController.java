package com.example.authms.controller;

import com.example.authms.dto.request.*;
import com.example.authms.dto.response.AccessTokenResponse;
import com.example.authms.dto.response.AuthResponse;
import com.example.authms.dto.response.UuidResponse;
import com.example.authms.exception.ExistEmailException;
import com.example.authms.exception.OtpTimeException;
import com.example.authms.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("user-signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String userSignUp(@RequestBody @Valid SignUpRequest signUpRequest) throws ExistEmailException, MessagingException {
        authService.userSignUp(signUpRequest);
        return "Təsdiqləmə kodu sizin e-mail ünvanınıza göndərildi. Zəhmət olmasa hesabınızı təsdiqləyin ";
    }

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody LoginRequest loginRequest) throws MessagingException {
        return authService.login(loginRequest);
    }

    @PostMapping("verify-account")
    public ResponseEntity<String> verifyUser(@RequestBody @Valid OtpDto otp) {
        authService.verifyAccount(otp);
        return ResponseEntity.ok("Success.Your account has activated." +
                " You can login a website");

    }

    @PostMapping("resend-otp")
    public ResponseEntity<String> resetOtp(@RequestParam String email) throws MessagingException {
        authService.regenerateOtp(email);
        return ResponseEntity.ok("OTP code sent to email address");
    }

    @PostMapping("forget-password")
    public ResponseEntity<String> forgetPassword(@RequestParam String email) throws MessagingException {
        authService.forgetPassword(email);
        return ResponseEntity.ok("OTP code sent to email address." +
                " You can reset password");
    }


    @PostMapping("refresh-token")
    @ResponseStatus(HttpStatus.OK)
    public AccessTokenResponse refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) {
        return authService.refreshToken(refreshTokenRequest);
    }

    @PostMapping("/verify-otp")
    @ResponseStatus(HttpStatus.OK)
    public UuidResponse verifyOtp(@RequestBody @Valid OtpDto dto) throws OtpTimeException {
        return authService.verifyOtp(dto);

    }

    @PostMapping("reset-password/{uuid}")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest,
                                                @PathVariable String uuid) throws ExistEmailException, OtpTimeException {
        authService.resetPassword(resetPasswordRequest, uuid);
        return ResponseEntity.ok("Password has been reset successfully." +
                " You can login a website with new password");
    }

}
