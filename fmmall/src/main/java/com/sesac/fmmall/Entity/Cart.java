package com.sesac.fmmall.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int cartId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart(User user) {
        this.user = user;
    }

    public void addCartItem(CartItem newCartItem) {
        if (newCartItem == null || newCartItem.getProduct() == null) {
            throw new IllegalArgumentException("장바구니에 추가할 상품 정보가 올바르지 않습니다.");
        }

        Product product = newCartItem.getProduct();
        int stock = product.getStockQuantity(); // 현재 상품 재고

        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getProduct() != null && Objects.equals(item.getProduct().getProductId(), product.getProductId()))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int totalQuantity = existingItem.getCartItemQuantity() + newCartItem.getCartItemQuantity();
            if (totalQuantity > stock) {
                throw new IllegalArgumentException("상품의 재고가 부족합니다.");
            }
            existingItem.updateQuantity(totalQuantity, this.user.getUserId());
        } else {
            if (newCartItem.getCartItemQuantity() > stock) {
                throw new IllegalArgumentException("상품의 재고가 부족합니다.");
            }
            cartItems.add(newCartItem);
            newCartItem.associateWithCart(this);
        }
    }

    public void removeCartItem(int cartItemId, int requesterUserId) {
        // 권한 검증
        if (this.user.getUserId() != requesterUserId) {
            throw new IllegalStateException("다른 사용자의 장바구니를 조작할 권한이 없습니다.");
        }

        this.cartItems.removeIf(item -> item.getCartItemId() == cartItemId);
    }

    public void clearCart() {
        this.cartItems.clear();
    }
}
