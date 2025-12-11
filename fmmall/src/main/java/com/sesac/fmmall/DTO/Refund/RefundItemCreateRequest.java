package com.sesac.fmmall.DTO.Refund;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundItemCreateRequest {

    private int orderItemId;
    private Integer refundQuantity;
}