package com.sesac.fmmall.DTO.Address;

import com.sesac.fmmall.Entity.Address;
import com.sesac.fmmall.Entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressSaveRequestDto {

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String zipcode;

    @NotBlank
    private String address1;

    private String address2;

    private String isDefault = "N";

    public Address toEntity(User user) {
        return Address.builder()
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .zipcode(zipcode)
                .address1(address1)
                .address2(address2)
                .isDefault(isDefault)
                .user(user)
                .build();
    }
}