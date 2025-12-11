package com.sesac.fmmall.DTO.Address;

import com.sesac.fmmall.Entity.Address;
import lombok.Getter;

@Getter
public class AddressResponseDto {

    private int id;
    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address1;
    private String address2;
    private String isDefault;

    public AddressResponseDto(Address address) {
        this.id = address.getAddressId();
        this.receiverName = address.getReceiverName();
        this.receiverPhone = address.getReceiverPhone();
        this.zipcode = address.getZipcode();
        this.address1 = address.getAddress1();
        this.address2 = address.getAddress2();
        this.isDefault = address.getIsDefault();
    }
}