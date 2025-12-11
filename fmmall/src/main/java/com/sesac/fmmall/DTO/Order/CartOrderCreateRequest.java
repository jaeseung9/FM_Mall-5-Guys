package com.sesac.fmmall.DTO.Order;

import lombok.*;

/**
 * 장바구니에 담긴 상품을 기반으로 주문을 생성할 때 사용하는 DTO.
 * - 어떤 배송지(addressId)를 쓸지
 * - 어떤 결제수단(paymentMethodId)을 쓸지
 * 정도만 알려주면 됨.
 * 실제 상품 목록/수량은 Cart / CartItem 에서 읽어온다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartOrderCreateRequest {

    /**
     * 사용할 배송지 ID
     * - null 이면 "기본 배송지" 사용
     */
    private Integer addressId;

    /**
     * 사용할 결제수단 ID
     * - null 이면 "기본 결제수단" 사용
     */
    private Integer paymentMethodId;
}
