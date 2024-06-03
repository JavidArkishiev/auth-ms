package com.example.authms.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.websocket.OnMessage;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "email boş ola bilməz")
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.(com|ru)$", message = "email düzgün deyil")
    private String email;

    @NotBlank(message = "şifrə boş ola bilməz")
    @Size(min = 6, message = "şifrə ən azı 6 simvoldan ibarət olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "şifrədə ən az bir böyük latın hərfi,bir kiçik latın hərfi və rəqəm istifadə olunmalıdır")
    private String password;
}
