package com.sesac.fmmall.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RowCategoryDTO {
    private int rowCategoryId;
    private String name;
    private int categoryId;

}
