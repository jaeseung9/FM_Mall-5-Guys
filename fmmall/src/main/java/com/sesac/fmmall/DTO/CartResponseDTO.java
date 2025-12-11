package com.sesac.fmmall.DTO;

import com.sesac.fmmall.DTO.CartItem.CartItemResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 응답 DTO")
public class CartResponseDTO {

    @Schema(description = "장바구니 ID", example = "1")
    private int cartId;
    @Schema(description = "장바구니 항목 목록")
    private List<CartItemResponseDTO> itemList;
    @Schema(description = "총 항목 수", example = "3")
    private int totalItemCount;
    @Schema(description = "총 가격", example = "30000")
    private int totalPrice;

}
