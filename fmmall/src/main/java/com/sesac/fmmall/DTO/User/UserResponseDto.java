package com.sesac.fmmall.DTO.User;

import com.sesac.fmmall.Entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private int id;
    private String loginId;
    private String userName;
    private String userPhone;
    private String role;

    public UserResponseDto(User user) {
        this.id = user.getUserId();
        this.loginId = user.getLoginId();
        this.userName = user.getUserName();
        this.userPhone = user.getUserPhone();
        this.role = user.getRole().getRole();
    }
}
