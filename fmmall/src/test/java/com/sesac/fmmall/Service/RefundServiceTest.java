package com.sesac.fmmall.Service;

import com.sesac.fmmall.Constant.ProductStatus;
import com.sesac.fmmall.Constant.RefundReasonCode;
import com.sesac.fmmall.Constant.RefundStatus;
import com.sesac.fmmall.Constant.RefundType;
import com.sesac.fmmall.Constant.UserRole;
import com.sesac.fmmall.Constant.YesNo;
import com.sesac.fmmall.DTO.Refund.RefundCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundItemCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundItemResponse;
import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import com.sesac.fmmall.Security.JwtAuthorizationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
@Transactional
class RefundServiceTest {

    @Autowired
    private RefundService refundService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefundRepository refundRepository;

    @Autowired
    private RefundItemRepository refundItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RowCategoryRepository rowCategoryRepository;

    // SecurityConfig 때문에 필요한 MockBean 들
    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    // BeanConfig 의 ModelMapper 대신 MockBean 으로 주입
    @MockBean
    private ModelMapper modelMapper;

    private User user;        // 일반 사용자
    private User admin;       // 관리자
    private Product product;
    private Order order;
    private OrderItem orderItem;
    private Payment payment;

    @BeforeEach
    void setUp() {

        // === 1) 유저 (USER / ADMIN) 생성 ===
        user = User.builder()
                .loginId("user1")
                .password("password")
                .userName("일반유저")
                .userPhone("010-1111-2222")
                .role(UserRole.USER)
                .build();
        user = userRepository.save(user);

        admin = User.builder()
                .loginId("admin1")
                .password("password")
                .userName("관리자")
                .userPhone("010-9999-9999")
                .role(UserRole.ADMIN)
                .build();
        admin = userRepository.save(admin);

        // === 2) 브랜드 / 카테고리 / 하위 카테고리 생성 ===
        Brand brand = Brand.builder()
                .name("테스트 브랜드")
                .build();
        brand = brandRepository.save(brand);

        Category category = Category.builder()
                .name("TV/가전")
                .build();
        category = categoryRepository.save(category);

        RowCategory rowCategory = RowCategory.builder()
                .name("OLED TV")
                .category(category)
                .build();
        rowCategory = rowCategoryRepository.save(rowCategory);

        // === 3) 상품 생성 (Product 엔티티 NOT NULL 필드들 모두 채우기) ===
        product = Product.builder()
                .name("테스트 상품")
                .price(10_000)
                .stockQuantity(100)
                .capacity("용량")
                .description("환불 테스트용 상품")
                .isInstallationRequired("Y")
                .productStatus(ProductStatus.ACTIVE)
                .modelName("REFUND-TEST-001")
                .brand(brand)
                .category(category)
                .rowCategory(rowCategory)
                .build();
        product = productRepository.save(product);

        // === 4) 주문 + 주문아이템 + 결제 생성 ===
        order = Order.builder()
                .receiverName("수령인")
                .receiverPhone("010-1234-5678")
                .zipcode("12345")
                .address1("서울시 테스트구")
                .address2("테스트로 101호")
                .totalPrice(0) // 나중에 계산되지만 필수라 일단 0
                .deliveryTrackingNumber(null)
                .createdAt(LocalDateTime.now())
                .user(user)
                .orderItems(new ArrayList<>())
                .refunds(new ArrayList<>())
                .build();
        order = orderRepository.save(order);

        orderItem = OrderItem.builder()
                .quantity(2)  // 주문 수량 2개
                .deliveryDate(null)
                .installationDate(null)
                .order(order)
                .product(product)
                .refundItems(new ArrayList<>())
                .build();
        orderItem = orderItemRepository.save(orderItem);
        order.getOrderItems().add(orderItem);

        payment = Payment.builder()
                .paymentMethodType("HyundaiCard")
                .paidAt(LocalDateTime.now())
                .order(order)
                .build();
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        // === 5) ModelMapper Mock -> 실제 매핑 위임 설정 ===
        given(modelMapper.map(any(Refund.class), eq(RefundResponse.class)))
                .willAnswer(invocation -> {
                    Refund source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true)
                            .setAmbiguityIgnored(true);

                    return real.map(source, RefundResponse.class);
                });

        given(modelMapper.map(any(RefundItem.class), eq(RefundItemResponse.class)))
                .willAnswer(invocation -> {
                    RefundItem source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true)
                            .setAmbiguityIgnored(true);

                    return real.map(source, RefundItemResponse.class);
                });
    }

    @Test
    @DisplayName("createRefund - 부분 환불 성공 (환불 수량 <= 주문 수량)")
    void createRefund_partial_success() {
        // given
        RefundItemCreateRequest itemReq = RefundItemCreateRequest.builder()
                .orderItemId(orderItem.getOrderItemId())
                .refundQuantity(1)   // 1개 환불
                .build();

        RefundCreateRequest request = RefundCreateRequest.builder()
                .orderId(order.getOrderId())
                .paymentId(payment.getPaymentId())
                .reasonCode(RefundReasonCode.CHANGE.name())
                .reasonDetail("단순 변심")
                .refundType(RefundType.PARTIAL.name())
                .items(List.of(itemReq))
                .build();

        // when
        RefundResponse response = refundService.createRefund(user.getUserId(), request);

        // then
        System.out.println("=== 🔥 RefundResponse ===");
        System.out.println("refundId=" + response.getRefundId());
        System.out.println("refundType=" + response.getRefundType());
        System.out.println("totalAmount=" + response.getTotalAmount());

        assertThat(response).isNotNull();
        assertThat(response.getRefundType()).isEqualTo(RefundType.PARTIAL.name());
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getRefundQuantity()).isEqualTo(1);

        // 실제 DB에 Refund / RefundItem 이 생성됐는지 확인
        // 이번에 생성된 refundId 기준으로만 검증
        Refund savedRefund = refundRepository.findById(response.getRefundId())
                .orElseThrow(() -> new AssertionError("생성된 Refund가 DB에 존재하지 않습니다."));

        assertThat(savedRefund.getTotalAmount()).isEqualTo(response.getTotalAmount());
        assertThat(savedRefund.getOrder().getOrderId()).isEqualTo(order.getOrderId());
        assertThat(savedRefund.getPayment().getPaymentId()).isEqualTo(payment.getPaymentId());

// 이 Refund에 연결된 RefundItem만 필터링해서 검증
        List<RefundItem> itemsForThisRefund = refundItemRepository.findAll().stream()
                .filter(ri -> ri.getRefund().getRefundId() == response.getRefundId())
                .toList();

        assertThat(itemsForThisRefund).hasSize(1);
        assertThat(itemsForThisRefund.get(0).getRefundQuantity()).isEqualTo(1);

    }

    @Test
    @DisplayName("createRefund - 환불 수량이 주문 수량 초과 시 예외 발생")
    void createRefund_exceedQuantity_throwException() {
        // given
        RefundItemCreateRequest itemReq = RefundItemCreateRequest.builder()
                .orderItemId(orderItem.getOrderItemId())
                .refundQuantity(3)   // 주문수량(2)보다 큼
                .build();

        RefundCreateRequest request = RefundCreateRequest.builder()
                .orderId(order.getOrderId())
                .paymentId(payment.getPaymentId())
                .reasonCode(RefundReasonCode.CHANGE.name())
                .reasonDetail("단순 변심")
                .refundType(RefundType.PARTIAL.name())
                .items(List.of(itemReq))
                .build();

        // when & then
        assertThatThrownBy(() -> refundService.createRefund(user.getUserId(), request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("환불 수량이 주문 수량을 초과합니다");
    }

    @Test
    @DisplayName("approveRefund - 관리자가 아니면 예외 발생")
    void approveRefund_nonAdmin_throwException() {
        // given
        int anyRefundId = 999;

        // when & then
        assertThatThrownBy(() -> refundService.approveRefund(anyRefundId, user.getUserId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("관리자만 환불을 승인할 수 있습니다.");
    }

    @Test
    @DisplayName("approveRefund - 관리자 승인 시 RefundItem 상태 REQUESTED -> APPROVED")
    void approveRefund_success() {
        // given: 사전 환불 데이터 생성
        Refund refund = Refund.builder()
                .reasonCode(RefundReasonCode.CHANGE.name())
                .reasonDetail("테스트 환불")
                .totalAmount(10_000)
                .refundType(RefundType.PARTIAL)
                .isTrue(YesNo.N)
                .order(order)
                .payment(payment)
                .refundItems(new ArrayList<>())
                .build();
        refund = refundRepository.save(refund);

        RefundItem refundItem = RefundItem.builder()
                .refundQuantity(1)
                .refundPrice(10_000)
                .refundStatus(RefundStatus.REQUESTED)
                .refund(refund)
                .orderItem(orderItem)
                .build();
        refundItem = refundItemRepository.save(refundItem);
        refund.getRefundItems().add(refundItem);

        // when
        RefundResponse response = refundService.approveRefund(refund.getRefundId(), admin.getUserId());

        // then
        assertThat(response).isNotNull();

        RefundItem changed = refundItemRepository.findById(refundItem.getRefundItemId())
                .orElseThrow();
        assertThat(changed.getRefundStatus()).isEqualTo(RefundStatus.APPROVED);
    }
}
