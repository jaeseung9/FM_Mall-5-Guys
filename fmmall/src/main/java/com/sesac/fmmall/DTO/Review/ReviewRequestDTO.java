package com.sesac.fmmall.DTO.Review;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReviewRequestDTO {
    private Double reviewRating;
    private String reviewContent;

    private int orderItemId;
}
