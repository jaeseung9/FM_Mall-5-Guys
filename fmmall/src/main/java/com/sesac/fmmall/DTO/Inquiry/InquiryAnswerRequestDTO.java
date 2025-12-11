package com.sesac.fmmall.DTO.Inquiry;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class InquiryAnswerRequestDTO {
    private String inquiryAnswerContent;
    private int inquiryId;
}
