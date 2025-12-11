package com.sesac.fmmall.Constant;

public enum RefundStatus {
    REQUESTED,   // 환불 신청됨
    APPROVED,    // 관리자 승인
    REJECTED,    // 관리자 거절
    COMPLETED    // 실제 환불 완료
}