package com.sesac.fmmall.Service;

import com.sesac.fmmall.Constant.RefundReasonCode;
import com.sesac.fmmall.Constant.RefundStatus;
import com.sesac.fmmall.Constant.RefundType;
import com.sesac.fmmall.Constant.UserRole;
import com.sesac.fmmall.Constant.YesNo;
import com.sesac.fmmall.DTO.Refund.RefundCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundItemCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundItemResponse;
import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RefundService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final RefundItemRepository refundItemRepository;
    private final UserRepository userRepository;   // 관리자 권한 체크용

    private final ModelMapper modelMapper;

    @Transactional
    public RefundResponse createRefund(Integer userId, RefundCreateRequest request) {

        // 1. 주문 검증
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("주문이 존재하지 않습니다. orderId=" + request.getOrderId()));

        if (order.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주문에 대해서만 환불을 요청할 수 있습니다.");
        }

        // 2. 결제 검증
        Payment payment = paymentRepository.findById(request.getPaymentId())
                .orElseThrow(() -> new IllegalArgumentException("결제 정보가 존재하지 않습니다. paymentId=" + request.getPaymentId()));

        if (payment.getOrder().getOrderId() != order.getOrderId()) {
            throw new IllegalArgumentException("주문과 결제 정보가 일치하지 않습니다.");
        }

        // 3. 환불 사유 코드 처리
        RefundReasonCode reasonCodeEnum;
        try {
            reasonCodeEnum = RefundReasonCode.valueOf(request.getReasonCode());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 환불 사유 코드입니다. reasonCode=" + request.getReasonCode()
            );
        }

        // 4. 환불 타입 처리
        RefundType refundType;
        try {
            refundType = RefundType.valueOf(request.getRefundType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 환불 타입입니다. refundType=" + request.getRefundType()
            );
        }

        // 5. 환불 상품 유효성
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("환불 상품이 없습니다.");
        }

        // 6. 주문상품별 기존 환불수량/이번 요청수량 합산을 위한 맵
        Map<Integer, Integer> alreadyRefundedByOrderItemId = new HashMap<>();
        Map<Integer, Integer> requestedQtyByOrderItemId = new HashMap<>();

        // 7. Refund 엔티티 생성 (아이템은 아래에서 add)
        Refund refund = Refund.builder()
                .reasonCode(reasonCodeEnum.name())
                .reasonDetail(request.getReasonDetail())
                .totalAmount(0)
                .refundType(refundType)
                .isTrue(YesNo.N)
                .order(order)
                .payment(payment)
                .build();

        // 8. RefundItem 생성 + 수량검증 + 금액 계산
        for (RefundItemCreateRequest itemReq : request.getItems()) {

            OrderItem orderItem = orderItemRepository.findById(itemReq.getOrderItemId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "주문 상품이 존재하지 않습니다. orderItemId=" + itemReq.getOrderItemId()));

            if (orderItem.getOrder().getOrderId() != order.getOrderId()) {
                throw new IllegalArgumentException("해당 주문에 속하지 않는 주문상품입니다. orderItemId=" + orderItem.getOrderItemId());
            }

            Integer refundQuantity = itemReq.getRefundQuantity();
            if (refundQuantity == null || refundQuantity < 1) {
                throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
            }

            int orderItemId = orderItem.getOrderItemId();

            // 기존까지 환불된 수량
            int alreadyRefunded = alreadyRefundedByOrderItemId.computeIfAbsent(
                    orderItemId,
                    id -> refundItemRepository.findByOrderItem(orderItem).stream()
                            .mapToInt(RefundItem::getRefundQuantity)
                            .sum()
            );

            // 이번 요청에서의 수량 누적
            requestedQtyByOrderItemId.merge(orderItemId, refundQuantity, Integer::sum);

            int afterTotal = alreadyRefunded + requestedQtyByOrderItemId.get(orderItemId);
            if (afterTotal > orderItem.getQuantity()) {
                throw new IllegalArgumentException(
                        "환불 수량이 주문 수량을 초과합니다. orderItemId=" + orderItemId);
            }

            int productPrice = orderItem.getProduct().getPrice();
            int refundPrice = productPrice * refundQuantity;

            RefundItem refundItem = RefundItem.builder()
                    .orderItem(orderItem)
                    .refundQuantity(refundQuantity)
                    .refundPrice(refundPrice)
                    .refundStatus(RefundStatus.REQUESTED)   // 최초 상태: REQUESTED(요청됨)
                    .refund(refund)
                    .build();

            refund.addRefundItem(refundItem);
        }

        // 9. FULL / PARTIAL 로직 검증
        boolean isFullRefund = isFullRefundForOrder(
                order,
                alreadyRefundedByOrderItemId,
                requestedQtyByOrderItemId
        );

        if (refundType == RefundType.FULL && !isFullRefund) {
            throw new IllegalArgumentException("환불 타입이 FULL이지만, 주문 전체 수량이 모두 환불되도록 선택되지 않았습니다.");
        }

        if (refundType == RefundType.PARTIAL && isFullRefund) {
            throw new IllegalArgumentException("환불 타입이 PARTIAL인데, 결과적으로 주문 전체가 모두 환불되도록 요청되었습니다.");
        }

        // 10. 총 환불 금액 계산
        refund.setTotalAmount(refund.calculateTotalAmount());

        Refund savedRefund = refundRepository.save(refund);

        return mapToRefundResponse(savedRefund);
    }

    /**
     * 주문 기준으로 "이번 요청까지 포함했을 때 전체가 다 환불되는지" 판단하는 헬퍼 메서드
     */
    private boolean isFullRefundForOrder(
            Order order,
            Map<Integer, Integer> alreadyRefundedByOrderItemId,
            Map<Integer, Integer> requestedQtyByOrderItemId
    ) {
        for (OrderItem orderItem : order.getOrderItems()) {

            int orderItemId = orderItem.getOrderItemId();

            int alreadyRefunded = alreadyRefundedByOrderItemId.getOrDefault(orderItemId, 0);
            int requestedQty = requestedQtyByOrderItemId.getOrDefault(orderItemId, 0);

            int afterTotal = alreadyRefunded + requestedQty;

            // 하나라도 수량이 다 안 되면 전체환불 아님
            if (afterTotal != orderItem.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 로그인 사용자의 전체 환불 내역 조회 (요약)
     */
    @Transactional
    public List<RefundSummaryResponse> getRefundsByUser(Integer userId) {

        // 1. 유저의 모든 주문 조회
        List<Order> orders = orderRepository.findByUser_UserId(userId);

        // 2. 주문에 달린 모든 환불 모으기
        List<Refund> allRefunds = new ArrayList<>();
        for (Order order : orders) {
            allRefunds.addAll(order.getRefunds());
        }

        // 3. 요약 DTO로 변환
        return allRefunds.stream()
                .map(this::mapToRefundSummaryResponse)
                .collect(Collectors.toList());
    }

    /**
     * 로그인 사용자의 특정 상품 기준 환불 내역 조회 (상세)
     */
    @Transactional
    public List<RefundResponse> getRefundsByUserAndProduct(Integer userId, Integer productId) {

        // productId 기준으로 주문상품 조회
        List<OrderItem> orderItems = orderItemRepository.findByProduct_ProductId(productId);

        Set<Integer> refundIdSet = new LinkedHashSet<>();
        List<Refund> resultRefunds = new ArrayList<>();

        for (OrderItem orderItem : orderItems) {

            // 다른 사람 주문이면 스킵
            if (orderItem.getOrder().getUser().getUserId() != userId) {
                continue;
            }

            // 해당 OrderItem에 연결된 RefundItem들 조회
            List<RefundItem> refundItems = refundItemRepository.findByOrderItem(orderItem);
            for (RefundItem refundItem : refundItems) {
                Refund refund = refundItem.getRefund();
                if (refundIdSet.add(refund.getRefundId())) {
                    resultRefunds.add(refund);
                }
            }
        }

        return resultRefunds.stream()
                .map(this::mapToRefundResponse)
                .collect(Collectors.toList());
    }

    /**
     * 환불 단건 상세 조회
     */
    @Transactional
    public RefundResponse getRefundDetail(Integer refundId) {

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "환불 정보가 존재하지 않습니다. refundId=" + refundId));

        return mapToRefundResponse(refund);
    }

    @Transactional
    public RefundResponse approveRefund(Integer refundId, Integer adminUserId) {

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. userId=" + adminUserId));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자만 환불을 승인할 수 있습니다.");
        }

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보가 존재하지 않습니다. refundId=" + refundId));

        for (RefundItem item : refund.getRefundItems()) {
            if (item.getRefundStatus() != RefundStatus.REQUESTED) {
                throw new IllegalStateException(
                        "REQUESTED 상태가 아닌 환불아이템이 포함되어 있어 승인할 수 없습니다. refundItemId=" + item.getRefundItemId());
            }
            item.changeStatus(RefundStatus.APPROVED);
        }

        return mapToRefundResponse(refund);
    }

    @Transactional
    public RefundResponse rejectRefund(Integer refundId, Integer adminUserId) {

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. userId=" + adminUserId));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자만 환불을 거절할 수 있습니다.");
        }

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보가 존재하지 않습니다. refundId=" + refundId));

        for (RefundItem item : refund.getRefundItems()) {
            if (item.getRefundStatus() != RefundStatus.REQUESTED) {
                throw new IllegalStateException(
                        "REQUESTED 상태가 아닌 환불아이템이 포함되어 있어 거절할 수 없습니다. refundItemId=" + item.getRefundItemId());
            }
            item.changeStatus(RefundStatus.REJECTED);
        }

        refund.setIsTrue(YesNo.N);

        return mapToRefundResponse(refund);
    }

    @Transactional
    public RefundResponse completeRefund(Integer refundId, Integer adminUserId) {

        User admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 정보를 찾을 수 없습니다. userId=" + adminUserId));

        if (admin.getRole() != UserRole.ADMIN) {
            throw new IllegalStateException("관리자만 환불 완료 처리를 할 수 있습니다.");
        }

        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("환불 정보가 존재하지 않습니다. refundId=" + refundId));

        for (RefundItem item : refund.getRefundItems()) {
            if (item.getRefundStatus() != RefundStatus.APPROVED
                    && item.getRefundStatus() != RefundStatus.COMPLETED) {
                throw new IllegalStateException(
                        "APPROVED 상태가 아닌 환불아이템이 포함되어 있어 완료 처리할 수 없습니다. refundItemId=" + item.getRefundItemId());
            }
            item.changeStatus(RefundStatus.COMPLETED);
        }

        refund.setIsTrue(YesNo.Y);

        return mapToRefundResponse(refund);
    }

    // ===================== 매핑 메서드 ===================== //

    /** 상세 조회용 매핑 */
    private RefundResponse mapToRefundResponse(Refund refund) {

        RefundResponse dto = modelMapper.map(refund, RefundResponse.class);

        dto.setRefundId(refund.getRefundId());
        dto.setReasonCode(refund.getReasonCode());
        dto.setReasonDetail(refund.getReasonDetail());
        dto.setTotalAmount(refund.getTotalAmount());
        dto.setRefundType(refund.getRefundType().name());
        dto.setIsTrue(refund.getIsTrue().name());
        dto.setOrderId(refund.getOrder().getOrderId());
        dto.setPaymentId(refund.getPayment().getPaymentId());

        List<RefundItemResponse> itemDtos = refund.getRefundItems().stream()
                .map(this::mapToRefundItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        return dto;
    }

    /** 목록(요약) 조회용 매핑 */
    private RefundSummaryResponse mapToRefundSummaryResponse(Refund refund) {

        return RefundSummaryResponse.builder()
                .refundId(refund.getRefundId())
                .refundType(refund.getRefundType().name())
                .totalAmount(refund.getTotalAmount())
                .isTrue(refund.getIsTrue().name())
                .build();
    }

    private RefundItemResponse mapToRefundItemResponse(RefundItem item) {

        RefundItemResponse dto = modelMapper.map(item, RefundItemResponse.class);

        dto.setRefundItemId(item.getRefundItemId());
        dto.setOrderItemId(item.getOrderItem().getOrderItemId());
        dto.setRefundQuantity(item.getRefundQuantity());
        dto.setRefundPrice(item.getRefundPrice());
        dto.setRefundStatus(item.getRefundStatus().name());

        return dto;
    }
}
