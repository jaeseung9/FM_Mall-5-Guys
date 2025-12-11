package com.sesac.fmmall.DTO.Refund;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundCreateRequest {

    private int orderId;
    private int paymentId;

    private String reasonCode;
    private String reasonDetail;


    private String refundType;


    private List<RefundItemCreateRequest> items;
}