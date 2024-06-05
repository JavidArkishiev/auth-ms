package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {


    @NotBlank(message = "email boş ola bilməz")
    @Pattern(regexp = "^[\\w.-]+@[\\w.-]+\\.(com|ru)$", message = "email düzgün deyil")
    private String email;

    @NotBlank(message = "ad boş ola bilməz")
    private String name;

    @NotBlank(message = "soyad can not be null")
    private String surname;

    @NotBlank(message = "şifrə boş ola bilməz")
    @Size(min = 6, message = "şifrə ən azı 6 simvoldan ibarət olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "şifrədə ən az bir böyük latın hərfi,bir kiçik latın hərfi və rəqəm istifadə olunmalıdır")
    private String password;

    @NotBlank(message = "şifrə boş ola bilməz")
    private String confirmPassword;
}