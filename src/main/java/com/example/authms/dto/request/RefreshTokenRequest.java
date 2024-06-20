package com.example.authms.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RefreshTokenRequest {
    @JsonProperty("Refresh_token")
    @NotBlank(message = "RefreshToken can not be null")
    private String refreshToken;
}
