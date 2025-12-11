package com.sesac.fmmall.DTO.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "장바구니 항목 수정 요청 DTO")
public class CartItemUpdateRequestDTO {
    @Schema(description = "변경할 수량", example = "3")
    private int quantity;
}
