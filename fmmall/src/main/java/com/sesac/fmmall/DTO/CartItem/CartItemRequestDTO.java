package com.sesac.fmmall.DTO.CartItem;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemRequestDTO {

    private int productId;
    private int cartItemQuantity;
}
