package com.sesac.fmmall.DTO.Inquiry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac.fmmall.Entity.Inquiry;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponseDTO {
    private int inquiryId;
    private String inquiryContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private int userId;
    private int productId;

    public static InquiryResponseDTO from(Inquiry inquiry) {
        return InquiryResponseDTO.builder()
                .inquiryId(inquiry.getInquiryId())
                .inquiryContent(inquiry.getInquiryContent())

                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .userId(inquiry.getUser().getUserId())
                .productId(inquiry.getProduct().getProductId())
                .build();
    }

//    public InquiryResponseDTO(Inquiry inquiry) {
//        this.inquiryId = inquiry.getInquiryId();
//        this.inquiryContent = inquiry.getInquiryContent();
//        this.createdAt = inquiry.getCreatedAt();
//        this.updatedAt = inquiry.getUpdatedAt();
//        this.userId = inquiry.getUser().getId();
//        this.productId = inquiry.getProduct().getId();
//
//    }
}
