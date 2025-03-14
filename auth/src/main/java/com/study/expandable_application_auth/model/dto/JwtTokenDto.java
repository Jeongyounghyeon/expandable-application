package com.study.expandable_application_auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class JwtTokenDto {

    String accessToken;
    String refreshToken;
}
