package com.sesac.fmmall.Entity;

import com.sesac.fmmall.Constant.ProductStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private int productId;

    @Column(name = "product_name", nullable = false, length = 200)
    private String name;

    @Column(name = "product_price", nullable = false)
    private Integer price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "capacity", length = 50)
    private String capacity;

    @Column(name = "size_inch", precision = 5, scale = 1)
    private BigDecimal sizeInch;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_installation_required", nullable = false, length = 1)
    private String isInstallationRequired;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_status", nullable = false, length = 20)
    private ProductStatus productStatus;

    @Column(name = "model_name", length = 100)
    private String modelName;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "row_category_id")
    private RowCategory rowCategory;

    public void modify(
            Category newCategory,
            RowCategory newRowCategory,
            Brand newBrand,
            String newProductName,
            int newPrice,
            String newDescription,
            String newIsInstallationRequired,
            ProductStatus newStatus
    ) {
        this.category = newCategory;
        this.rowCategory = newRowCategory;
        this.brand = newBrand;
        this.name = newProductName;
        this.price = newPrice;
        this.description = newDescription;
        this.isInstallationRequired = newIsInstallationRequired;
        this.productStatus = newStatus;
    }
}
