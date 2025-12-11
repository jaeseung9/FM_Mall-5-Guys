package com.sesac.fmmall.DTO.CartItem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 항목 응답 DTO")
public class CartItemResponseDTO {

    @Schema(description = "장바구니 항목 ID", example = "1")
    private int cartItemId;
    @Schema(description = "상품 ID", example = "101")
    private int productId;
    @Schema(description = "상품명", example = "멋진 신발")
    private String productName;
    @Schema(description = "상품 가격", example = "50000")
    private int productPrice;
    @Schema(description = "상품 이미지 URL", example = "http://example.com/product.jpg")
    private String productImage;
    @Schema(description = "장바구니에 담긴 수량", example = "2")
    private int cartItemQuantity;
    @Schema(description = "총 가격 (상품 가격 * 수량)", example = "100000")
    private int totalPrice;
    @Schema(description = "선택 상태 (예: 'Y', 'N')", example = "Y")
    private String checkStatus;
    @Schema(description = "장바구니에 추가된 날짜")
    private LocalDateTime addDate;
}
