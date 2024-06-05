package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
    @NotBlank(message = "şifrə boş ola bilməz")
    @Size(min = 6, message = "şifrə ən azı 6 simvoldan ibarət olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "şifrədə ən az bir böyük latın hərfi,bir kiçik latın hərfi və rəqəm istifadə olunmalıdır")
    private String currentPassword;

    @NotBlank(message = "şifrə boş ola bilməz")
    @Size(min = 6, message = "şifrə ən azı 6 simvoldan ibarət olmalıdır")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "şifrədə ən az bir böyük latın hərfi,bir kiçik latın hərfi və rəqəm istifadə olunmalıdır")
    private String newPassword;

    @NotBlank(message = "şifrə boş ola bilməz")
    private String confirmationPassword;
}
