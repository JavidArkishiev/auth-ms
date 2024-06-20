package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@ToString
public class DeletePasswordDto {
    @NotBlank(message = "Şifrə boş ola bilməz")
    @Size(min = 8, message = "Şifrə ən azı 8 simvoldan ibarət olmalıdır")
    private String password;
}
