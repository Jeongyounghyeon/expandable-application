package com.study.expandable_application_auth.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.study.expandable_application_auth.model.dto.JwtTokenDto;
import lombok.Value;

@Value
public class AuthenticateResponse {

    @JsonProperty("access_token")
    String accessToken;

    @JsonProperty("refresh_token")
    String refreshToken;

    public static AuthenticateResponse from(JwtTokenDto jwtTokenDto) {
        return new AuthenticateResponse(jwtTokenDto.getAccessToken(), jwtTokenDto.getRefreshToken());
    }
}
