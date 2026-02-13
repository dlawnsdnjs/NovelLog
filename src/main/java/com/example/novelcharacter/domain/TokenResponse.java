package com.example.novelcharacter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    String accessToken;
    String refreshToken;
}
