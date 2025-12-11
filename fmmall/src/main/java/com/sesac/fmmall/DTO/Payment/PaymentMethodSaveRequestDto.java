package com.sesac.fmmall.DTO.Payment;

import com.sesac.fmmall.Entity.PaymentMethod;
import com.sesac.fmmall.Entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentMethodSaveRequestDto {

    @NotBlank
    private String cardCompany;

    @NotBlank
    private String maskedCardNumber;

    private Boolean isDefault = false;

    public PaymentMethod toEntity(User user) {
        return PaymentMethod.builder()
                .cardCompany(cardCompany)
                .maskedCardNumber(maskedCardNumber)
                .isDefault(isDefault)
                .user(user)
                .build();
    }
}
