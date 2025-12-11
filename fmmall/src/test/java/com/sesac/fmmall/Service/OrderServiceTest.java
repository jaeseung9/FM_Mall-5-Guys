package com.sesac.fmmall.Service;

import com.sesac.fmmall.Constant.ProductStatus;
import com.sesac.fmmall.Constant.UserRole;
import com.sesac.fmmall.DTO.Order.CartOrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderItemCreateRequest;
import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.Entity.*;
import com.sesac.fmmall.Repository.*;
import com.sesac.fmmall.Security.JwtAuthorizationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    // ==== Repositories ====
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RowCategoryRepository rowCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private OrderRepository orderRepository;

    // SecurityConfig 때문에 필요한 MockBean 들
    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    // ==== 테스트에서 공통으로 사용할 엔티티들 ====
    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private PaymentMethod paymentMethod;
    private Address address;

    @BeforeEach
    void setUp() {
        // 1) 유저 생성
        user = User.builder()
                .loginId("testUser")
                .password("encoded-password")
                .userName("테스트 사용자")
                .userPhone("010-1111-2222")
                .role(UserRole.USER)
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        // 2) 브랜드 / 카테고리 / 하위 카테고리
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

        // 3) 상품 (재고 10개)
        product = Product.builder()
                .name("테스트 TV")
                .price(1_000_000)
                .stockQuantity(10)
                .capacity("75인치")
                .sizeInch(BigDecimal.valueOf(75.0))
                .description("테스트용 TV 입니다.")
                .isInstallationRequired("Y")
                .productStatus(ProductStatus.ACTIVE)
                .modelName("TV-TEST-001")
                .brand(brand)
                .category(category)
                .rowCategory(rowCategory)
                .build();
        product = productRepository.save(product);

        // 4) 배송지(Address)
        address = Address.builder()
                .receiverName("테스트 수령인")
                .receiverPhone("010-9999-8888")
                .zipcode("12345")
                .address1("서울시 강남구 테헤란로 123")
                .address2("101동 1001호")
                .isDefault("Y")              // String("Y"/"N") 기준
                .user(user)
                .build();
        address = addressRepository.save(address);

        // 5) 장바구니 + 장바구니 아이템
        cart = new Cart(user);      // Cart(User user) 생성자 사용
        cart = cartRepository.save(cart);

        cartItem = CartItem.createCartItem(product, 2);  // 수량 2개
        cart.addCartItem(cartItem);                      // 양방향 연관관계 편의 메서드
        cart = cartRepository.save(cart);                // cascade 로 cartItem 함께 저장

        // 6) 결제수단(PaymentMethod)
        paymentMethod = PaymentMethod.builder()
                .cardCompany("HyundaiCard")
                .maskedCardNumber("****-****-****-1234")
                .isDefault(true)
                .user(user)
                .build();
        paymentMethod = paymentMethodRepository.save(paymentMethod);
    }

    // ========================================================================
    // ① 장바구니 기반 주문 생성 테스트 (CartOrderCreateRequest 사용)
    // ========================================================================
    @Test
    @DisplayName("장바구니 기반 주문 - 정상 주문 시 재고 차감 & 주문/결제/주문상품 생성 & DTO 반환")
    @Transactional
    void createOrderFromCart_success() {

        int beforeStock = product.getStockQuantity();

        // 1) 요청 DTO 준비
        CartOrderCreateRequest request = CartOrderCreateRequest.builder()
                .addressId(address.getAddressId())
                .paymentMethodId(paymentMethod.getPaymentMethodId())
                .build();

        // 2) 서비스 호출
        // 시그니처 예시: public OrderResponse createOrderFromCart(int userId, CartOrderCreateRequest request)
        OrderResponse response = orderService.createOrderFromCart(user.getUserId(), request);

        // 3) 반환 DTO 검증
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isGreaterThan(0);
        assertThat(response.getReceiverName()).isEqualTo(address.getReceiverName());
        assertThat(response.getTotalPrice())
                .isEqualTo(product.getPrice() * cartItem.getCartItemQuantity());

        // 4) 실제 DB에 저장된 Order / OrderItem / Payment 검증
        Order savedOrder = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new AssertionError("저장된 주문을 찾을 수 없습니다."));

        assertThat(savedOrder.getOrderItems()).hasSize(1);
        assertThat(savedOrder.getOrderItems().get(0).getQuantity())
                .isEqualTo(cartItem.getCartItemQuantity());

        assertThat(savedOrder.getPayment()).isNotNull();
        assertThat(savedOrder.getPayment().getOrder().getOrderId())
                .isEqualTo(savedOrder.getOrderId());

        // 5) 재고 차감 검증
        Product updatedProduct = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new AssertionError("상품을 찾을 수 없습니다."));

        assertThat(updatedProduct.getStockQuantity())
                .isEqualTo(beforeStock - cartItem.getCartItemQuantity());
    }

    // ========================================================================
    // ② 직접 상품 리스트로 주문 생성 테스트 (OrderCreateRequest 사용)
    // ========================================================================
    @Test
    @DisplayName("직접 상품 리스트 기반 주문 - 정상 주문 시 재고 차감 & 주문/결제/주문상품 생성 & DTO 반환")
    @Transactional
    void createOrderDirect_success() {

        int orderQuantity = 3;
        int beforeStock = product.getStockQuantity();

        // 1) 주문 상품 요청 DTO
        OrderItemCreateRequest itemRequest = OrderItemCreateRequest.builder()
                .productId(product.getProductId())
                .quantity(orderQuantity)
                .build();

        // 2) 전체 주문 생성 요청 DTO
        OrderCreateRequest request = OrderCreateRequest.builder()
                .addressId(address.getAddressId())
                .items(List.of(itemRequest))
                .paymentMethodId(paymentMethod.getPaymentMethodId())
                .build();

        // 3) 서비스 호출
        // 시그니처 예시: public OrderResponse createOrder(int userId, OrderCreateRequest request)
        OrderResponse response = orderService.createOrder(user.getUserId(), request);

        // 4) 응답 DTO 검증
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isGreaterThan(0);
        assertThat(response.getTotalPrice())
                .isEqualTo(product.getPrice() * orderQuantity);
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getProductId())
                .isEqualTo(product.getProductId());
        assertThat(response.getItems().get(0).getQuantity())
                .isEqualTo(orderQuantity);

        // 5) 실제 DB 검증
        Order savedOrder = orderRepository.findById(response.getOrderId())
                .orElseThrow(() -> new AssertionError("저장된 주문을 찾을 수 없습니다."));

        assertThat(savedOrder.getOrderItems()).hasSize(1);
        assertThat(savedOrder.getOrderItems().get(0).getProduct().getProductId())
                .isEqualTo(product.getProductId());
        assertThat(savedOrder.getOrderItems().get(0).getQuantity())
                .isEqualTo(orderQuantity);

        assertThat(savedOrder.getPayment()).isNotNull();

        // 6) 재고 차감 확인
        Product updatedProduct = productRepository.findById(product.getProductId())
                .orElseThrow(() -> new AssertionError("상품을 찾을 수 없습니다."));

        assertThat(updatedProduct.getStockQuantity())
                .isEqualTo(beforeStock - orderQuantity);
    }
}
