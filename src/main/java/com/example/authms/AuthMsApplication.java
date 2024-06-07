package com.example.authms;

import com.example.authms.entity.Role;
import com.example.authms.entity.User;
import com.example.authms.mapper.UserMapper;
import com.example.authms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class AuthMsApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(AuthMsApplication.class, args);
    }

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createAdminIfNotExists();
    }

    private void createAdminIfNotExists() {
        String adminEmail = "daviddavidov041@gmail.com";

        Optional<User> adminOptional = userRepository.findByEmail(adminEmail);

        if (adminOptional.isEmpty()) {
            User user = new User();
            user.setEmail(adminEmail);
            user.setName("Anar");
            user.setSurname("Eyvazov");
            user.setCreationDate(LocalDateTime.now());
            user.setEnabled(true);
            user.setOtp(userMapper.generateRandomOtp());
            user.setOtpGeneratedTime(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode("davidD123"));
            user.setRoles(mapRole());

            userRepository.save(user);
            System.out.println("Admin has created");

        } else {
            System.out.println("Admin already created");

        }

    }

    public List<Role> mapRole() {
        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setName("ADMIN");
        roles.add(role);
        return roles;
    }
}
