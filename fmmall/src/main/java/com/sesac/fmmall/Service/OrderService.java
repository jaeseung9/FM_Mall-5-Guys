package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Order.CartOrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemResponse;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.DTO.Order.OrderSummaryResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.DTO.Settlement.PaymentSummaryResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 주문 비즈니스 로직을 담당하는 Service
 * - 주문 생성 (직접 상품 지정)
 * - 장바구니 기반 주문 생성
 * - 주문 조회 (전체 / 단건 / 상품 기준)
 * - 주문 취소
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final AddressRepository addressRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final CartRepository cartRepository;        // ✅ 장바구니 조회용

    private final ModelMapper modelMapper;

    // =========================================================
    // 1. 주문 생성 (사용자가 상품/수량을 직접 넘기는 방식)
    // =========================================================

    /**
     * 주문 생성 (주문 + 결제 동시 처리)
     * - URL: POST /Order/insert
     * - 요청 바디: OrderCreateRequest
     * - 흐름:
     *   1) userId로 User 조회
     *   2) OrderCreateRequest 안의 items 를 돌면서 상품/수량 검증 + 재고 차감
     *   3) Order & OrderItem 생성/저장
     *   4) 결제수단(PaymentMethod) 선택 + Payment 생성
     */
    @Transactional
    public OrderResponse createOrder(Integer userId, OrderCreateRequest request) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        // 2. 주문 상품 검증
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("주문 상품이 없습니다.");
        }

        // 3. 배송지 선택 (addressId 있으면 해당 주소, 없으면 기본 배송지 사용)
        Address shippingAddress;
        if (request.getAddressId() != null) {
            shippingAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "배송지 정보가 존재하지 않습니다. addressId=" + request.getAddressId()));

            // 다른 사람 주소 사용 방지
            if (shippingAddress.getUser().getUserId() != userId) {
                throw new IllegalArgumentException("본인의 배송지 정보만 사용할 수 있습니다.");
            }

        } else {
            // 기본 배송지(Y) 조회
            shippingAddress = addressRepository
                    .findByUser_UserIdAndIsDefault(userId, "Y")
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기본 배송지가 설정되어 있지 않습니다. addressId를 지정하거나 기본 배송지를 등록해주세요."));
        }

        // 4. 주문 엔티티 생성 (주소 정보는 Address에서 가져옴)
        Order order = Order.builder()
                .receiverName(shippingAddress.getReceiverName())
                .receiverPhone(shippingAddress.getReceiverPhone())
                .zipcode(shippingAddress.getZipcode())
                .address1(shippingAddress.getAddress1())
                .address2(shippingAddress.getAddress2())
                .totalPrice(0)   // 나중에 계산
                .deliveryTrackingNumber(null)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        // 5. 주문상품 생성 + 재고 체크/차감
        for (OrderItemCreateRequest itemReq : request.getItems()) {

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "존재하지 않는 상품입니다. productId=" + itemReq.getProductId()));

            Integer qty = itemReq.getQuantity();
            if (qty == null || qty < 1) {
                throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
            }

            // 재고 부족 체크
            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException(
                        "상품 재고가 부족합니다. productId=" + product.getProductId()
                                + ", stock=" + product.getStockQuantity()
                                + ", requested=" + qty
                );
            }

            // 재고 차감
            product.setStockQuantity(product.getStockQuantity() - qty);

            // OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .deliveryDate(null)
                    .installationDate(null)
                    .build();

            // 양방향 연관 관계 설정 (order <-> orderItem)
            order.addOrderItem(orderItem);
        }

        // 6. 주문 총 금액 계산
        order.setTotalPrice(order.calculateTotalPrice());

        // 7. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 8. 결제수단 선택 (paymentMethodId 있으면 그 카드, 없으면 기본 카드)
        PaymentMethod paymentMethod;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "결제수단 정보가 존재하지 않습니다. paymentMethodId=" + request.getPaymentMethodId()));

            if (paymentMethod.getUser().getUserId() != userId) {
                throw new IllegalArgumentException("본인의 결제수단만 사용할 수 있습니다.");
            }
        } else {
            // 기본 결제수단(true) 사용
            paymentMethod = paymentMethodRepository
                    .findByUser_UserIdAndIsDefault(userId, true)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기본 결제수단이 설정되어 있지 않습니다. paymentMethodId를 지정하거나 기본 결제수단을 등록해주세요."));
        }

        // 9. 결제 생성 (결제 성공 가정)
        Payment payment = Payment.builder()
                .paymentMethodType(paymentMethod.getCardCompany())  // 예: "HyundaiCard"
                .paidAt(LocalDateTime.now())
                .order(savedOrder)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        // 10. DTO 변환 후 반환
        return mapToOrderResponse(savedOrder);
    }

    // =========================================================
    // 2. 장바구니 기반 주문 생성 (Cart → Order)
    // =========================================================

    /**
     * 장바구니 기반 주문 생성
     * - URL: POST /Order/insertFromCart
     * - 요청 바디: CartOrderCreateRequest
     * - 흐름:
     *   1) userId로 User + Cart 조회
     *   2) Cart 안의 CartItem 중 checkStatus = 'Y' 인 것만 주문에 포함
     *   3) 배송지/결제수단 선택
     *   4) Order & OrderItem 생성 + 재고 차감
     *   5) Payment 생성
     *   6) 주문에 사용된 CartItem 은 장바구니에서 제거
     */
    @Transactional
    public OrderResponse createOrderFromCart(Integer userId, CartOrderCreateRequest request) {

        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. userId=" + userId));

        // 2. 장바구니 조회
        Cart cart = cartRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니가 존재하지 않습니다. userId=" + userId));

        // 3. 주문에 사용할 CartItem 선택
        //    - checkStatus = 'Y' 인 것만 주문에 포함
        //    - 만약 "장바구니 전체 주문"으로 바꾸고 싶으면 이 filter 제거 후 cart.getCartItems() 전체 사용
        List<CartItem> selectedItems = cart.getCartItems().stream()

                .collect(Collectors.toList());



        // 4. 배송지 선택 (addressId 있으면 해당 주소, 없으면 기본 배송지 사용)
        Address shippingAddress;
        if (request.getAddressId() != null) {
            shippingAddress = addressRepository.findById(request.getAddressId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "배송지 정보가 존재하지 않습니다. addressId=" + request.getAddressId()));

            if (shippingAddress.getUser().getUserId() != userId) {
                throw new IllegalArgumentException("본인의 배송지 정보만 사용할 수 있습니다.");
            }
        } else {
            shippingAddress = addressRepository
                    .findByUser_UserIdAndIsDefault(userId, "Y")
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기본 배송지가 설정되어 있지 않습니다. addressId를 지정하거나 기본 배송지를 등록해주세요."));
        }

        // 5. 주문 엔티티 생성
        Order order = Order.builder()
                .receiverName(shippingAddress.getReceiverName())
                .receiverPhone(shippingAddress.getReceiverPhone())
                .zipcode(shippingAddress.getZipcode())
                .address1(shippingAddress.getAddress1())
                .address2(shippingAddress.getAddress2())
                .totalPrice(0)   // 나중에 계산
                .deliveryTrackingNumber(null)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        // 6. CartItem -> OrderItem 변환 + 재고 체크/차감
        for (CartItem cartItem : selectedItems) {

            Product product = cartItem.getProduct();
            int qty = cartItem.getCartItemQuantity();

            if (product == null) {
                throw new IllegalStateException("장바구니에 상품 정보가 존재하지 않습니다. cartItemId=" + cartItem.getCartItemId());
            }

            if (qty < 1) {
                throw new IllegalArgumentException("상품 수량은 1개 이상이어야 합니다.");
            }

            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException(
                        "상품 재고가 부족합니다. productId=" + product.getProductId()
                                + ", stock=" + product.getStockQuantity()
                                + ", requested=" + qty
                );
            }

            // 재고 차감
            product.setStockQuantity(product.getStockQuantity() - qty);

            // OrderItem 생성
            OrderItem orderItem = OrderItem.builder()
                    .product(product)
                    .quantity(qty)
                    .deliveryDate(null)
                    .installationDate(null)
                    .build();

            // 주문과 양방향 연관관계 설정
            order.addOrderItem(orderItem);
        }

        // 7. 주문 총 금액 계산
        order.setTotalPrice(order.calculateTotalPrice());

        // 8. 주문 저장
        Order savedOrder = orderRepository.save(order);

        // 9. 결제수단 선택 (paymentMethodId 있으면 그것, 없으면 기본 결제수단)
        PaymentMethod paymentMethod;
        if (request.getPaymentMethodId() != null) {
            paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "결제수단 정보가 존재하지 않습니다. paymentMethodId=" + request.getPaymentMethodId()));

            if (paymentMethod.getUser().getUserId() != userId) {
                throw new IllegalArgumentException("본인의 결제수단만 사용할 수 있습니다.");
            }
        } else {
            paymentMethod = paymentMethodRepository
                    .findByUser_UserIdAndIsDefault(userId, true)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "기본 결제수단이 설정되어 있지 않습니다. paymentMethodId를 지정하거나 기본 결제수단을 등록해주세요."));
        }

        // 10. 결제 생성 (결제 성공 가정)
        Payment payment = Payment.builder()
                .paymentMethodType(paymentMethod.getCardCompany())
                .paidAt(LocalDateTime.now())
                .order(savedOrder)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        savedOrder.setPayment(savedPayment);

        // 11. 주문에 사용된 CartItem 들을 장바구니에서 제거
        //     Cart.cartItems는 orphanRemoval = true 이므로
        //     리스트에서 제거하면 DB에서도 자동 삭제된다.
        cart.getCartItems().removeAll(selectedItems);

        // 12. DTO 변환 후 반환
        return mapToOrderResponse(savedOrder);
    }

    // =========================================================
    // 3. 주문 조회 (전체 / 단건 / 상품 기준)
    // =========================================================

    /**
     * 특정 사용자의 전체 주문 목록 (요약 정보)
     * - URL: GET /Order/findAll
     */
    @Transactional
    public List<OrderSummaryResponse> getOrdersByUser(Integer userId) {

        List<Order> orders = orderRepository.findByUser_UserId(userId);

        return orders.stream()
                .map(this::mapToOrderSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 특정 사용자의 특정 주문 상세 조회
     * - URL: GET /Order/findOne/{orderId}
     */
    @Transactional
    public OrderResponse getOrderDetail(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + orderId));

        // 다른 사람 주문 접근 방지
        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문만 조회할 수 있습니다.");
        }

        return mapToOrderResponse(order);
    }

    /**
     * 특정 사용자가 특정 상품을 주문한 이력 조회 (상세)
     * - URL: GET /Order/findByProduct/{productId}
     */
    @Transactional
    public List<OrderResponse> getOrdersByUserAndProduct(Integer userId, Integer productId) {

        // productId 기준으로 주문상품 조회
        List<OrderItem> orderItems = orderItemRepository.findByProduct_ProductId(productId);

        // userId로 필터링 + 주문 중복 제거
        Map<Integer, Order> uniqueOrders = new LinkedHashMap<>();

        for (OrderItem item : orderItems) {
            Order order = item.getOrder();
            if (order.getUser().getUserId() == userId) {
                uniqueOrders.putIfAbsent(order.getOrderId(), order);
            }
        }

        return uniqueOrders.values().stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // =========================================================
    // 4. 주문 취소
    // =========================================================

    /**
     * 주문 취소
     * - URL: PUT /Order/cancel/{orderId}
     * - 조건:
     *   1) 본인 주문만 취소 가능
     *   2) 환불 이력이 있는 주문은 취소 불가
     *   3) 배송일이 null 이거나 "오늘 이후"인 경우만 취소 가능
     *   => 이미 배송이 시작되었으면 환불로 처리해야 한다.
     */
    @Transactional
    public void cancelOrder(Integer orderId, Integer userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + orderId));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문만 취소할 수 있습니다.");
        }

        // 환불 이력이 있으면 취소 불가
        if (!order.getRefunds().isEmpty()) {
            throw new IllegalStateException("환불 이력이 있는 주문은 취소할 수 없습니다.");
        }

        LocalDate today = LocalDate.now();

        // 배송일 기준 취소 가능 여부 판단 (배송일이 null이거나 오늘 이후인 경우만 취소 가능)
        boolean canCancel = order.getOrderItems().stream()
                .allMatch(item ->
                        item.getDeliveryDate() == null ||
                                item.getDeliveryDate().isAfter(today)
                );

        if (!canCancel) {
            throw new IllegalStateException("이미 배송이 시작되었거나 완료된 상품이 있어 주문 취소가 불가능합니다. 환불을 이용해주세요.");
        }

        // 재고 복구
        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
        }

        // 결제 삭제
        Payment payment = order.getPayment();
        if (payment != null) {
            paymentRepository.delete(payment);
            order.setPayment(null);
        }

        // 주문 삭제 (OrderItem, Refund는 cascade + orphanRemoval로 함께 삭제)
        orderRepository.delete(order);
    }

    // =========================================================
    // 5. 엔티티 → DTO 매핑 메서드
    // =========================================================

    /**
     * 상세 조회용 DTO 매핑
     */
    private OrderResponse mapToOrderResponse(Order order) {

        // 기본 필드는 ModelMapper로 매핑
        OrderResponse dto = modelMapper.map(order, OrderResponse.class);

        // 이름이 다른 필드 수동 보정
        dto.setOrderId(order.getOrderId());
        dto.setUserId(order.getUser().getUserId());

        // 주문상품 리스트 매핑
        List<OrderItemResponse> itemDtos = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        // 결제 요약 매핑
        PaymentSummaryResponse paymentDto = null;
        if (order.getPayment() != null) {
            paymentDto = modelMapper.map(order.getPayment(), PaymentSummaryResponse.class);
            paymentDto.setPaymentId(order.getPayment().getPaymentId());
        }
        dto.setPayment(paymentDto);

        // 환불 요약 리스트 매핑
        List<RefundSummaryResponse> refundDtos = order.getRefunds().stream()
                .map(this::mapToRefundSummaryResponse)
                .collect(Collectors.toList());
        dto.setRefunds(refundDtos);

        return dto;
    }

    /**
     * 목록(요약) 조회용 DTO 매핑
     */
    private OrderSummaryResponse mapToOrderSummaryResponse(Order order) {

        // 주문에 포함된 총 상품 수량
        int totalQuantity = order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        // 주문에 포함된 상품명 목록 (중복 제거)
        List<String> productNames = order.getOrderItems().stream()
                .map(oi -> oi.getProduct().getName())   // ⚠ Product 엔티티 필드명에 맞게 수정
                .distinct()
                .collect(Collectors.toList());

        return OrderSummaryResponse.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .createdAt(order.getCreatedAt())
                .totalQuantity(totalQuantity)
                .productNames(productNames)
                .build();
    }

    /**
     * 주문상품 상세 DTO 매핑
     */
    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {

        OrderItemResponse dto = modelMapper.map(item, OrderItemResponse.class);

        dto.setOrderItemId(item.getOrderItemId());
        dto.setProductId(item.getProduct().getProductId());
        dto.setProductName(item.getProduct().getName());   // ⚠ Product 엔티티 필드명에 맞게
        dto.setProductPrice(item.getProduct().getPrice()); // ⚠ Product 엔티티 필드명에 맞게
        dto.setLineTotalPrice(item.calculateLineTotalPrice());

        return dto;
    }

    /**
     * 환불 요약 DTO 매핑
     */
    private RefundSummaryResponse mapToRefundSummaryResponse(Refund refund) {

        return RefundSummaryResponse.builder()
                .refundId(refund.getRefundId())
                .refundType(refund.getRefundType().name())
                .totalAmount(refund.getTotalAmount())
                .isTrue(refund.getIsTrue().name())
                .build();
    }
}
