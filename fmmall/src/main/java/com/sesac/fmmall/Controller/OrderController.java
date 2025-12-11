package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Order.CartOrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Order.OrderSummaryResponse;
import com.sesac.fmmall.Service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문 API")
@RestController
@RequestMapping("/Order")
@RequiredArgsConstructor
public class OrderController extends BaseController {

    private final OrderService orderService;

    @Operation(summary = "상품 즉시 주문 생성", description = "단일 상품에 대한 주문을 생성하고 결제를 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 재고 부족, 결제 정보 오류)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<OrderResponse> insertOrder(
            @RequestBody OrderCreateRequest request
    ) {
        OrderResponse response = orderService.createOrder(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "장바구니 상품 주문 생성", description = "장바구니에 담긴 상품들에 대한 주문을 생성하고 결제를 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "주문 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 재고 부족, 결제 정보 오류)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 장바구니를 찾을 수 없음")
    })
    @PostMapping("/insertFromCart")
    public ResponseEntity<OrderResponse> insertOrderFromCart(
            @RequestBody CartOrderCreateRequest request
    ) {
        OrderResponse response = orderService.createOrderFromCart(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 주문 목록 조회", description = "현재 로그인된 사용자의 모든 주문 내역을 요약하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<OrderSummaryResponse>> findAllByUser() {
        List<OrderSummaryResponse> responses = orderService.getOrdersByUser(getCurrentUserId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "특정 주문 상세 조회", description = "주문 ID로 특정 주문의 상세 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 주문에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 또는 사용자를 찾을 수 없음")
    })
    @GetMapping("/findOne/{orderId}")
    public ResponseEntity<OrderResponse> findOne(
            @PathVariable Integer orderId
    ) {
        OrderResponse response = orderService.getOrderDetail(orderId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품별 주문 내역 조회", description = "특정 상품에 대한 현재 사용자의 모든 주문 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "주문 내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
    })
    @GetMapping("/findByProduct/{productId}")
    public ResponseEntity<List<OrderResponse>> findByProduct(
            @PathVariable Integer productId
    ) {
        List<OrderResponse> responses = orderService.getOrdersByUserAndProduct(getCurrentUserId(), productId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "주문 취소", description = "특정 주문을 취소합니다. '결제완료' 상태에서만 가능합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "400", description = "주문 취소가 불가능한 상태"),
            @ApiResponse(responseCode = "403", description = "해당 주문에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "주문 또는 사용자를 찾을 수 없음")
    })
    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable Integer orderId
    ) {
        orderService.cancelOrder(orderId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
