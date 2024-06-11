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
    @JsonProperty("refresh_token")
    @NotBlank(message = "refreshToken can not be null")
    private String refreshToken;
}
