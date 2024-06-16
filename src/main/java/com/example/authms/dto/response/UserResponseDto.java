package com.example.authms.dto.response;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserResponseDto {

    private Long userId;
    private String fullName;

    private String email;

}
