package com.sesac.fmmall.DTO.Payment;

import com.sesac.fmmall.Entity.PaymentMethod;
import lombok.Getter;

@Getter
public class PaymentMethodResponseDto {

    private int id;
    private String cardCompany;
    private String maskedCardNumber;
    private Boolean isDefault;

    public PaymentMethodResponseDto(PaymentMethod pm) {
        this.id = pm.getPaymentMethodId();
        this.cardCompany = pm.getCardCompany();
        this.maskedCardNumber = pm.getMaskedCardNumber();
        this.isDefault = pm.getIsDefault();
    }
}
