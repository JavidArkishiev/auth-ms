package com.example.authms.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AccessTokenRequest {
    @JsonProperty("access_token")
    private String accessToken;
}
