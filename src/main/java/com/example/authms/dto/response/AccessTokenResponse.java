package com.example.authms.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
public class AccessTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
}
