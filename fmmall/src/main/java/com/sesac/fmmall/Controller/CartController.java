package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.CartItem.CartItemCreateRequestDTO;
import com.sesac.fmmall.DTO.CartItem.CartItemUpdateRequestDTO;
import com.sesac.fmmall.DTO.CartResponseDTO;
import com.sesac.fmmall.Service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "장바구니 API")
@RestController
@RequestMapping("/Cart")
@RequiredArgsConstructor
public class CartController extends BaseController {

    private final CartService cartService;

    @Operation(summary = "장바구니 상품 추가", description = "장바구니에 상품을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 상품 ID 또는 수량 누락)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<CartResponseDTO> addCartItem(
            @RequestBody CartItemCreateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.createCartItem(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 상품의 수량을 변경합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수량 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 수량 누락)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 장바구니 항목을 찾을 수 없음")
    })
    @PutMapping("/modify/{cartItemId}")
    public ResponseEntity<CartResponseDTO> modifyCartItem(
            @PathVariable int cartItemId,
            @RequestBody CartItemUpdateRequestDTO requestDTO
    ) {
        CartResponseDTO response = cartService.updateCartItemQuantity(getCurrentUserId(), cartItemId, requestDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 장바구니 항목을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable int cartItemId
    ) {
        cartService.removeCartItem(getCurrentUserId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장바구니 전체 삭제", description = "장바구니의 모든 상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "장바구니 전체 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "장바구니 목록 조회", description = "장바구니에 담긴 상품 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<CartResponseDTO> findAllCartItems() {

        CartResponseDTO response = cartService.findAllCartItems(getCurrentUserId());
        return ResponseEntity.ok(response);
    }
}
