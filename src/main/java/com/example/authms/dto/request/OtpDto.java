package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpDto {

    @NotBlank(message= "Otp boş ola bilməz")
    @Size(min = 6, max = 6, message = "Otp kodu 6 simvoldan ibarət olmalıdır")
    private String otp;
}
