package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.InquiryAnswer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Integer> {
//    /* 전달 받은 정렬 기준으로 조회 (페이징) */
//    Page<Inquiry> findAll(Pageable pageable);

    Page<InquiryAnswer> findAllByUser_UserId(int userUserId, Pageable pageable);
    Page<InquiryAnswer> findAllByInquiry_InquiryId(int inquiryId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "ALTER TABLE inquiry_answer AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();

}
