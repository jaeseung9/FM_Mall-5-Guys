package com.sesac.fmmall.DTO.User;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;
}