package com.sesac.fmmall.Entity;

import com.sesac.fmmall.Constant.RefundStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refund_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefundItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_item_id")
    private int refundItemId;

    @Column(name = "refund_quantity", nullable = false)
    private Integer refundQuantity;

    @Column(name = "refund_price", nullable = false)
    private Integer refundPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "refund_status", length = 20, nullable = false)
    private RefundStatus refundStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id", nullable = false)
    private Refund refund;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    public void changeStatus(RefundStatus status) {
        this.refundStatus = status;
    }
}