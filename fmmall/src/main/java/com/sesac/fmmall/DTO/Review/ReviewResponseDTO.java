package com.sesac.fmmall.DTO.Review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sesac.fmmall.Entity.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDTO {
    private int reviewId;
    private Double reviewRating;
    private String reviewContent;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private int userId;
    private int orderItemId;

    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .reviewRating(review.getReviewRating())
                .reviewContent(review.getReviewContent())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .userId(review.getUser().getUserId())
                .orderItemId(review.getOrderItem().getOrderItemId())
                .build();

    }
}
