package com.example.authms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "otp", unique = true)
    private String otp;

    @Column(name = "uuid", unique = true)
    private String UUID;

    @Column(name = "uuid_generated_time")
    private LocalDateTime uuidGeneratedTimme;

    @Column(name = "otp_generated_time")
    private LocalDateTime otpGeneratedTime = LocalDateTime.now();

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "failed_attempt", nullable = false)
    private int failedAttempt;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "account_non_locked", nullable = false)
    private boolean accountNonLocked = true;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(roles -> new SimpleGrantedAuthority(roles.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}
