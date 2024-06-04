package com.example.authms.service;

import com.example.authms.dto.*;
import com.example.authms.entity.User;
import com.example.authms.exception.ExistEmailException;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.mapper.UserMapper;
import com.example.authms.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public void userSignUp(SignUpRequest signUpRequest) throws ExistEmailException, MessagingException {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ExistEmailException("“Bu e-poçt ünvanı üzrə sistemdə istifadəçi mövcuddur");
        }

        if (!signUpRequest.getPassword().matches(signUpRequest.getConfirmPassword())) {
            throw new UserNotFoundException("hər iki şifrə eyni olmalıdır");
        }

        User userEntity = userMapper.mapToEntity(signUpRequest);
        userRepository.save(userEntity);
        sendVerificationEmail(userEntity.getEmail(), userEntity.getOtp());

    }

    public AuthResponse login(LoginRequest loginRequest) throws MessagingException {

        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("e-mail və ya şifrə yanlışdır"));

        if (!user.isEnabled()) {
            regenerateOtp(user.getEmail());
            throw new UserNotFoundException("Sizin hesabınız aktiv deyil." +
                    "Zəhmət olmasa əvvəlcə hesabınızı aktiv edin." +
                    "Təsdiqləmə kodu sizin e-mail ünvanınıza göndərildi");
        }
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new UserNotFoundException("e-mail və ya şifrə yanlışdır");
        }
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
        return AuthResponse.builder().accessToken(jwt).refreshToken(refreshToken)
                .build();
    }

    private void sendVerificationEmail(String email, String otp) throws MessagingException {
        String subject = "Email Verification";
        String body = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Email Verification</title><style>body {font-family: Arial, sans-serif;background-color: #f4f4f4;margin: 0;padding: 0;}.container {max-width: 600px;margin: 0 auto;padding: 20px;background-color: #ffffff;border-radius: 8px;box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}.header {background-color: #007bff;color: #ffffff;padding: 20px;text-align: center;border-top-left-radius: 8px;border-top-right-radius: 8px;}.content {padding: 20px;}.otp {font-size: 24px;color: #333333;text-align: center;margin-top: 20px;}</style></head><body><div class=\"container\"><div class=\"header\"><h2>Email Verification</h2></div><div class=\"content\"><p>Thank you for signing up! To verify your email, please use the following OTP</p><div class=\"otp\"><strong>" + otp + "</strong></div></div></div></body></html>";
        emailService.sendEmail(email, subject, body);
    }

    public void verifyAccount(String otp) {

        User user = userRepository.findByOtp(otp)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));

        if (!otp.equals(user.getOtp())) {
            throw new UserNotFoundException("kod yanlışdır");

        }
        if (Duration.between(user.getOtpGeneratedTime()
                        , LocalDateTime.now()).
                getSeconds() < 5 * 60) {
            user.setEnabled(true);
            userRepository.save(user);

        } else throw new
                UserNotFoundException("opt time is over.please regenerateOtp");

    }

    public void regenerateOtp(String email) throws MessagingException {
        String otp = userMapper.generateRandomOtp();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        sendVerificationEmail(user.getEmail(), otp);

    }

    public void forgetPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("istifadəçi tapılmadı"));

        regenerateOtp(user.getEmail());
        userRepository.save(user);

    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = userRepository.findByOtp(resetPasswordRequest.getOtp())
                .orElseThrow(() -> new UserNotFoundException("User not fond"));
        if (!user.getOtp().equals(resetPasswordRequest.getOtp())) {
            throw new UserNotFoundException("otp is not equals");
        }

        if (!resetPasswordRequest.getNewPassword().matches(resetPasswordRequest.getConfirmNewPassword())) {
            throw new UserNotFoundException("hər iki şifrə eyni olmalıdır");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

    }

    public void changePassword(Principal principal, ChangePasswordRequest request) {
        var user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UserNotFoundException("cari şifrə doğru deyil");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new UserNotFoundException("hər iki şifrə eyni olmalıdır");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }

}

