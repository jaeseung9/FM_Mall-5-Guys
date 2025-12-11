package com.sesac.fmmall.DTO.Product;

import com.sesac.fmmall.Constant.ProductStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private int productId;
    private String productName;
    private int productPrice;
    private int stockQuantity;
    private String capacity;
    private BigDecimal sizeInch;
    private String description;

    private ProductStatus productStatus;
    private String modelName;

    private int brandId;
    private int categoryId;

    private String isInstallationRequired;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

}
