package com.sesac.fmmall.DTO.Order;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {

    private int orderId;              // 주문 번호
    private Integer totalPrice;       // 주문 총 금액
    private LocalDateTime createdAt;  // 주문 일시

    // 목록에서 간단히 보여줄 정보들
    private int totalQuantity;        // 주문 상품 전체 수량 합
    private List<String> productNames; // 이 주문에 포함된 전체 상품 이름 리스트
}
