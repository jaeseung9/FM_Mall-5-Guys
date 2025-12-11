package com.sesac.fmmall.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private int orderItemId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    @Column(name = "installation_date")
    private LocalDate installationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // product_id FK -> Product 엔티티가 있다면 ManyToOne으로 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RefundItem> refundItems = new ArrayList<>();

    public int calculateLineTotalPrice() {
        if (product == null || product.getPrice() == null || quantity == null) {
            throw new IllegalStateException("라인 금액을 계산할 수 없습니다. 상품/수량 정보가 없습니다.");
        }
        return product.getPrice() * quantity;
    }
}