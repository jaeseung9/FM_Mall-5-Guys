package com.sesac.fmmall.DTO.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponseDto {

    private String accessToken;
    private String tokenType;   // "Bearer"
    private String loginId;
    private String role;
}