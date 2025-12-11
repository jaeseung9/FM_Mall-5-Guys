package com.sesac.fmmall.DTO.Order;


import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemResponse {

    private int orderItemId;

    private int productId;
    private String productName;
    private Integer productPrice;
    private Integer quantity;
    private Integer lineTotalPrice;

    private LocalDate deliveryDate;
    private LocalDate installationDate;
}