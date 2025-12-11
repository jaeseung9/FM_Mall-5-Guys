package com.sesac.fmmall.DTO.User;

import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Constant.UserRole;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSaveRequestDto {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String userName;

    @NotBlank
    private String userPhone;

    public User toEntity() {
        return User.builder()
                .loginId(loginId)
                .password(password)
                .userName(userName)
                .userPhone(userPhone)
                .role(UserRole.USER)
                .build();
    }
}