package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.CartItem.CartItemCreateRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemResponseDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemUpdateRequestDTO;
import com.sesac.fmmall.DTO.CartResponseDTO;
import com.sesac.fmmall.Entity.Cart;
import com.sesac.fmmall.Entity.CartItem;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.CartItemRepository;
import com.sesac.fmmall.Repository.CartRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public CartResponseDTO createCartItem(int userId, CartItemCreateRequestDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(requestDTO.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(user)));

        CartItem newCartItem = CartItem.createCartItem(product, requestDTO.getQuantity());
        cart.addCartItem(newCartItem);

        // 수정: cart 저장 추가
        cartRepository.save(cart);

        return findAllCartItems(userId);
    }

    @Transactional
    public CartResponseDTO updateCartItemQuantity(int userId, int cartItemId, CartItemUpdateRequestDTO requestDTO) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니 상품을 찾을 수 없습니다."));

        cartItem.updateQuantity(requestDTO.getQuantity(), userId);

        return findAllCartItems(userId);
    }

    @Transactional
    public void removeCartItem(int userId, int cartItemId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 장바구니를 찾을 수 없습니다."));

        cart.removeCartItem(cartItemId, userId);
    }

    @Transactional
    public void clearCart(int userId) {
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 장바구니를 찾을 수 없습니다."));
        cart.clearCart();
    }

    public CartResponseDTO findAllCartItems(int userId) {
        Optional<Cart> optCart = cartRepository.findByUser_UserId(userId);

        if (optCart.isEmpty()) {
            return CartResponseDTO.builder()
                    .cartId(0)
                    .itemList(Collections.emptyList())
                    .totalItemCount(0)
                    .totalPrice(0)
                    .build();
        }

        Cart cart = optCart.get();
        List<CartItemResponseDTO> cartItemList = cart.getCartItems().stream()
                .map(cartItem -> {
                    CartItemResponseDTO cartItemResponseDTO = modelMapper.map(cartItem, CartItemResponseDTO.class);
                    Product product = cartItem.getProduct();

                    cartItemResponseDTO.setProductId(product.getProductId());
                    cartItemResponseDTO.setProductName(product.getName());
                    cartItemResponseDTO.setProductPrice(product.getPrice());
                    cartItemResponseDTO.setTotalPrice(product.getPrice() * cartItem.getCartItemQuantity());

                    return cartItemResponseDTO;
                })
                .collect(Collectors.toList());

        int totalPrice = cartItemList.stream()
                .mapToInt(CartItemResponseDTO::getTotalPrice)
                .sum();

        return CartResponseDTO.builder()
                .cartId(cart.getCartId())
                .itemList(cartItemList)
                .totalItemCount(cartItemList.size())
                .totalPrice(totalPrice)
                .build();
    }
}