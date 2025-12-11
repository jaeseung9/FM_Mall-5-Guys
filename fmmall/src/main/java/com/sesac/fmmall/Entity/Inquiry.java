package com.sesac.fmmall.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "inquiry")
@Getter

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int inquiryId;
    private String inquiryContent;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
//    private int userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
//    private int productId;


    public void modifyContent(String inquiryContent) {
        this.inquiryContent = inquiryContent;
        // db에서는 제대로 들어가지만, jpa에서는 바로 값을 못 가져옴. 그래서 직접 값을 대입하는 형식으로 사용
        // 수정 시간이라 오차가 1초 정도 발생해도 크게 상관없을 듯.
        this.updatedAt = LocalDateTime.now();
    }


}