package com.sesac.fmmall.DTO.Inquiry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class InquiryRequestDTO {
    private String inquiryContent;
    private int productId;
}
