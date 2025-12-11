package com.sesac.fmmall.DTO.Order;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequest {

    // 선택: 지정 배송지 ID (없으면 기본 배송지 사용)
    private Integer addressId;

    // 주문 상품 목록
    private List<OrderItemCreateRequest> items;

    // 선택: 지정 결제수단 ID (없으면 기본 결제수단 사용)
    private Integer paymentMethodId;
}
