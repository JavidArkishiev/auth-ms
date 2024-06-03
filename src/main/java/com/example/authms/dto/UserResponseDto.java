package com.example.authms.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserResponseDto {

    private Long userId;
    private String name;

    private String surname;

    private String email;

}
