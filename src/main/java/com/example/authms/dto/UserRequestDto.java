package com.example.authms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDto {
    private String name;
    private String surname;

    @NotBlank(message = "email boş ola bilməz")
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.(com|ru)$", message = "email düzgün deyil")
    private String email;

}
