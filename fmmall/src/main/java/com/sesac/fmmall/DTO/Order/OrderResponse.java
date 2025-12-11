package com.sesac.fmmall.DTO.Order;

import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.DTO.Settlement.PaymentSummaryResponse;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private int orderId;

    private String receiverName;
    private String receiverPhone;
    private String zipcode;
    private String address1;
    private String address2;

    private Integer totalPrice;
    private String deliveryTrackingNumber;

    private LocalDateTime createdAt;

    private int userId;

    private List<OrderItemResponse> items;

    private PaymentSummaryResponse payment;

    private List<RefundSummaryResponse> refunds;
}