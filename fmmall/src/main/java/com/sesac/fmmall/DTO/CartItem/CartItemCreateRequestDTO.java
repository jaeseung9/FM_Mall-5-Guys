package com.sesac.fmmall.DTO.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "장바구니 항목 생성 요청 DTO")
public class CartItemCreateRequestDTO {
    @Schema(description = "상품 ID", example = "1")
    private int productId;
    @Schema(description = "수량", example = "2")
    private int quantity;
}
