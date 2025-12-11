package com.sesac.fmmall.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "receiver_name", length = 50, nullable = false)
    private String receiverName;

    @Column(name = "receiver_phone", length = 50, nullable = false)
    private String receiverPhone;

    @Column(name = "zipcode", length = 5, nullable = false)
    private String zipcode;

    @Column(name = "address1", length = 255, nullable = false)
    private String address1;

    @Column(name = "address2", length = 255, nullable = false)
    private String address2;

    @Column(name = "total_price", nullable = false)
    private Integer totalPrice;

    @Column(name = "delivery_tracking_number", length = 50)
    private String deliveryTrackingNumber;

    @Column(name = "created_at", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;



    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private Payment payment;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Refund> refunds = new ArrayList<>();

    // 🔹 주문 아이템 추가 (양방향 연관관계 관리)
    public void addOrderItem(OrderItem orderItem) {
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // 🔹 환불 추가 (양방향)
    public void addRefund(Refund refund) {
        this.refunds.add(refund);
        refund.setOrder(this);
    }

    // 🔹 주문 총 금액 계산 (상품가격 * 수량)
    public int calculateTotalPrice() {
        if (orderItems == null) return 0;
        return orderItems.stream()
                .mapToInt(OrderItem::calculateLineTotalPrice)
                .sum();
    }
}