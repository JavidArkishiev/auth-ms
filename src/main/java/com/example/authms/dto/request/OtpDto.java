package com.example.authms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OtpDto {

    @NotBlank(message= "otp boş ola bilməz")
    @Size(min = 6, max = 6, message = "otp kodu 6 simvoldan ibarət olmalıdır")
    private String otp;
}
