package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Otp boş ola bilməz")
    @Size(min = 6, max = 6, message = "Otp kodu 6 simvoldan ibarət olmalıdır")
    private String otp;
    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 8, message = "Şifrə ən azı 8 simvoldan ibarət olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Şifrədə ən az bir böyük latın hərfi,bir kiçik latın hərfi və rəqəm istifadə olunmalıdır")
    private String newPassword;

    @NotBlank(message = "Şifrə boş ola bilməz")
    private String confirmNewPassword;

}
