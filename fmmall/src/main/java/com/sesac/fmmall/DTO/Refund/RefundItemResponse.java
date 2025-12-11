package com.sesac.fmmall.DTO.Refund;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundItemResponse {

    private int refundItemId;

    private int orderItemId;
    private Integer refundQuantity;
    private Integer refundPrice;
    private String refundStatus;
}
