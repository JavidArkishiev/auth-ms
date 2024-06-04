package com.example.authms.controller;

import com.example.authms.dto.*;
import com.example.authms.exception.ExistEmailException;
import com.example.authms.service.AuthService;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("auth")
public class AuthController {
    private final AuthService authService;
    @CrossOrigin(origins = "https://auth-ms-99dc7b517339.herokuapp.com")
    @PostMapping("user-signUp")
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
    public ResponseEntity<String> verifyUser(@RequestParam String otp) {
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
                " You can reset password. Note OTP timeLimit is 2 minute");
    }

    @PostMapping("reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {
        authService.resetPassword(resetPasswordRequest);
        return ResponseEntity.ok("Password has been reset successfully." +
                " You can login a website with new password");
    }

    @PutMapping("change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest request,
                                                 Principal principal) {
        authService.changePassword(principal, request);
        return ResponseEntity.ok("Success");

    }
}
