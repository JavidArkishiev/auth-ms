package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 8, message = "Şifrə ən azı 8 simvoldan ibarət olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Şifrədə ən az bir böyük latın hərfi,bir kiçik latın hərfi və rəqəm istifadə olunmalıdır")
    private String newPassword;

    @NotBlank(message = "Şifrə boş ola bilməz")
    private String confirmNewPassword;

}
