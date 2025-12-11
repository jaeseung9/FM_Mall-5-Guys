package com.sesac.fmmall.DTO.Product;

import com.sesac.fmmall.Constant.ProductStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    private String productName;
    private int productPrice;
    private int stockQuantity;
    private String capacity;
    private BigDecimal sizeInch;
    private String description;
    private ProductStatus productStatus;
    private String modelName;
    private String isInstallationRequired;
    private int brandId;
    private int categoryId;
    private int rowCategoryId;

    public Integer getRowCategoryCode() {
        return this.rowCategoryId;
    }

    public Integer getCategoryCode() {
        return this.categoryId;
    }
}
