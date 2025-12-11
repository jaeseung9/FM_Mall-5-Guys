package com.sesac.fmmall.Entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartItemId;

    @Column(name = "cart_item_quantity")
    private int cartItemQuantity = 1;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime addDate;

    @Column(length = 1)
    private String checkStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public static CartItem createCartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("상품은 필수입니다.");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("수량은 1개 이상이어야 합니다.");
        }
        if (quantity > product.getStockQuantity()) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다.");
        }
        CartItem cartItem = new CartItem();
        cartItem.product = product;
        cartItem.cartItemQuantity = quantity;
        cartItem.checkStatus = "N";
        return cartItem;
    }

    public void updateQuantity(int newQuantity, int requesterUserId) {

        if (this.getCart().getUser().getUserId() != requesterUserId) {
            throw new IllegalStateException("다른 사용자의 장바구니 상품을 수정할 권한이 없습니다.");
        }
        if (newQuantity < 1) {
            throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
        }
        if (this.product != null && newQuantity > this.product.getStockQuantity()) {
            throw new IllegalArgumentException("상품의 재고가 부족합니다.");
        }

        this.cartItemQuantity = newQuantity;
    }

    public void updateCheckStatus(String newCheckStatus, int requesterUserId) {
        if (this.getCart().getUser().getUserId() != requesterUserId) {
            throw new IllegalStateException("다른 사용자의 장바구니 상품을 수정할 권한이 없습니다.");
        }

        if (!"Y".equals(newCheckStatus) && !"N".equals(newCheckStatus)) {
            throw new IllegalArgumentException("checkStatus는 'Y' 또는 'N' 이어야 합니다.");
        }

        this.checkStatus = newCheckStatus;
    }

    public void associateWithCart(Cart cart) {
        this.cart = cart;
    }
}
