package com.example.authms.service;

import com.example.authms.dto.request.*;
import com.example.authms.dto.response.AccessTokenResponse;
import com.example.authms.dto.response.AuthResponse;
import com.example.authms.dto.response.UuidResponse;
import com.example.authms.entity.User;
import com.example.authms.exception.OtpTimeException;
import com.example.authms.exception.UserNotFoundException;
import com.example.authms.exception.ExistEmailException;
import com.example.authms.mapper.UserMapper;
import com.example.authms.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.LinkedList;
import java.util.Queue;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final Queue<User> addQueue = new LinkedList<>();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();


    public void userSignUp(SignUpRequest signUpRequest) throws ExistEmailException, MessagingException {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ExistEmailException("Bu e-poçt ünvanı üzrə sistemdə istifadəçi mövcuddur");
        }

        if (!signUpRequest.getPassword().matches(signUpRequest.getConfirmPassword())) {
            throw new ExistEmailException("Hər iki şifrə eyni olmalıdır");
        }

        User userEntity = userMapper.mapToEntity(signUpRequest);
        userRepository.save(userEntity);
        addQueue.add(userEntity);

    }

    @Scheduled(fixedRate = 2000)
    public void processSignUpQueue() throws MessagingException {
        User userEntity;
        while ((userEntity = addQueue.poll()) != null) {
            sendVerificationEmail(userEntity.getEmail(), userEntity.getOtp());
        }
    }


    public AuthResponse login(LoginRequest loginRequest) throws MessagingException {
        var user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UserNotFoundException("E-mail və ya şifrə yanlışdır"));

        if (!user.isEnabled()) {
            regenerateOtp(user.getEmail());
            throw new UserNotFoundException("Sizin hesabınız aktiv deyil. " +
                    "Zəhmət olmasa əvvəlcə hesabınızı aktiv edin. " +
                    "Təsdiqləmə kodu sizin e-mail ünvanınıza göndərildi");
        }

        if (!user.isAccountNonLocked()) {
            if (unlock(user)) {
            } else {
                throw new UserNotFoundException("Həddindən çox giriş cəhdi biraz sonra yenidən cəhd edin");
            }
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                    loginRequest.getPassword()));
            resetFailedAttempts(user.getEmail());
            var jwt = jwtService.generateToken(user);
            var refreshToken = jwtService.generateRefreshToken(user);
            return AuthResponse.builder().accessToken(jwt).refreshToken(refreshToken).build();
        } catch (AuthenticationException e) {

            increaseFailedAttempts(user);
            if (user.getFailedAttempt() >= 5) {
                lock(user);
                throw new UserNotFoundException("Həddindən çox giriş cəhdi biraz sonra yenidən cəhd edin");
            }
            throw new UserNotFoundException("E-mail və ya şifrə yanlışdır");
        }
    }


    private void sendVerificationEmail(String email, String otp) throws MessagingException {
        String subject = "Email Verification";
        String body = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Email Verification</title><style>body {font-family: Arial, sans-serif;background-color: #f4f4f4;margin: 0;padding: 0;}.container {max-width: 600px;margin: 0 auto;padding: 20px;background-color: #ffffff;border-radius: 8px;box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}.header {background-color: #007bff;color: #ffffff;padding: 20px;text-align: center;border-top-left-radius: 8px;border-top-right-radius: 8px;}.content {padding: 20px;}.otp {font-size: 24px;color: #333333;text-align: center;margin-top: 20px;}</style></head><body><div class=\"container\"><div class=\"header\"><h2>Email Verification</h2></div><div class=\"content\"><p>Thank you for signing up! To verify your email, please use the following OTP</p><div class=\"otp\"><strong>" + otp + "</strong></div></div></div></body></html>";
        emailService.sendEmail(email, subject, body);
    }

    public void sendSecurityAlertEmail(String email) throws MessagingException {
        String subject = "Security Alert: Account Login Attempt";
        String body = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Security Alert</title><style>body {font-family: Arial, sans-serif;background-color: #f4f4f4;margin: 0;padding: 0;}.container {max-width: 600px;margin: 0 auto;padding: 20px;background-color: #ffffff;border-radius: 8px;box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);}.header {background-color: #dc3545;color: #ffffff;padding: 20px;text-align: center;border-top-left-radius: 8px;border-top-right-radius: 8px;}.content {padding: 20px;}.alert {font-size: 18px;color: #333333;margin-top: 20px;text-align: center;}</style></head><body><div class=\"container\"><div class=\"header\"><h2>Security Alert</h2></div><div class=\"content\"><p>Dear user,</p><p>We have detected a login attempt to your account. If this was you, you can safely ignore this email. If you did not attempt to log in, please secure your account immediately.</p><div class=\"alert\"><strong>Account Login Attempt Detected</strong></div></div></div></body></html>";
        emailService.sendEmail(email, subject, body);
    }

    public void verifyAccount(OtpDto otpDto) throws OtpTimeException, ExistEmailException {

        User user = userRepository.findByOtp(otpDto.getOtp())
                .orElseThrow(() -> new OtpTimeException("Kod yanlışdır"));
        if (user.isEnabled()) {
            throw new ExistEmailException("Bu hesab aktivdir");
        }

        if (Duration.between(user.getOtpGeneratedTime()
                        , LocalDateTime.now()).
                getSeconds() > 3 * 60) {
            throw new UserNotFoundException("Otp kodun istifadə müddəti bitmişdir. " +
                    "Zəhmət olmasa yenidən otp kodu əldə edin");
        }
        user.setEnabled(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);
    }

    public void regenerateOtp(String email) throws MessagingException {
        String otp = userMapper.generateRandomOtp();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now());
        userRepository.save(user);
        sendVerificationEmail(user.getEmail(), otp);

    }

    public void forgetPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));

        regenerateOtp(user.getEmail());
        userRepository.save(user);

    }


    public void increaseFailedAttempts(User user) throws MessagingException {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
        if (user.getFailedAttempt() == 3) {
            sendSecurityAlertEmail(user.getEmail());
        }
    }

    public void lock(User user) {
        user.setAccountNonLocked(false);
        user.setLockTime(LocalDateTime.now());
        userRepository.save(user);
    }

    public void resetFailedAttempts(String email) {
        User user = userRepository.findByEmail(email)
                .orElse(null);
        if (user != null) {
            user.setFailedAttempt(0);
            userRepository.save(user);
        }

    }

    public boolean unlock(User user) {
        LocalDateTime now = LocalDateTime.now();

        if (user.getLockTime().plusMinutes(15).isBefore(now)) {
            user.setAccountNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public AccessTokenResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {

        var userEmail = jwtService.extractUsername(refreshTokenRequest.getRefreshToken());
        var user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("İstifadəçi tapılmadı"));
        if (jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)) {
            var accessToken = jwtService.generateToken(user);
            return AccessTokenResponse.builder()
                    .accessToken(accessToken)
                    .build();
        }
        return null;
    }

    public UuidResponse verifyOtp(OtpDto dto) throws UserNotFoundException, OtpTimeException {
        User user = userRepository.findByOtp(dto.getOtp())
                .orElseThrow(() -> new OtpTimeException("Kod yanlışdır"));

        if (Duration.between(user.getOtpGeneratedTime(),
                LocalDateTime.now()).getSeconds() > 3 * 60) {
            throw new OtpTimeException("Otp kodun istifadə müddəti bitmişdir. Zəhmət olmasa otp kodu yenidən əldə edin");
        }

        String uuid = generateLongRandomString();
        user.setUUID(uuid);
        user.setUuidGeneratedTimme(LocalDateTime.now());
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);
        return UuidResponse.builder()
                .uuid(uuid).build();

    }

    private String generateLongRandomString() {
        byte[] randomBytes = new byte[64];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public void resetPassword(ResetPasswordRequest resetPasswordRequest, String uuid) throws ExistEmailException, OtpTimeException {
        User user = userRepository.findByUUID(uuid)
                .orElseThrow(() -> new UserNotFoundException("Uuid yanlışdır"));
        if (Duration.between(user.getUuidGeneratedTimme(),
                LocalDateTime.now()).getSeconds() > 3 * 60) {
            throw new OtpTimeException("Uuid istifadə müddəti bitmişdir");
        }

        if (!resetPasswordRequest.getNewPassword().matches(resetPasswordRequest.getConfirmNewPassword())) {
            throw new ExistEmailException("Hər iki şifrə eyni olmalıdır");
        }
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
        user.setUUID(null);
        user.setUuidGeneratedTimme(null);
        userRepository.save(user);

    }
}

