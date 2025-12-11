package com.sesac.fmmall.DTO.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestDto {

    private String userName;
    private String userPhone;
    private String password;  // 비밀번호 변경 시
}