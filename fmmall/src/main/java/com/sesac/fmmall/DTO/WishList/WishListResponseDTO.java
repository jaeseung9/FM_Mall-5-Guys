package com.sesac.fmmall.DTO.WishList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac.fmmall.Entity.WishList;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishListResponseDTO {

    private int wishListId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;

    private int userId;
    private int productId;

    private boolean isAdded;


    public static WishListResponseDTO from(WishList wishList) {
        return WishListResponseDTO.builder()
                .wishListId(wishList.getWishListId())
                .createdAt(wishList.getCreatedAt())
                .userId(wishList.getUser().getUserId())
                .productId(wishList.getProduct().getProductId())
                .isAdded(true)
                .build();
    }

    public static WishListResponseDTO removedDTO() {
        return WishListResponseDTO.builder()
                .isAdded(false)
                .build();
    }
}
