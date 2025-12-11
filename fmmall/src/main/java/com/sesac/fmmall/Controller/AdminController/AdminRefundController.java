package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.Service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[관리자] 환불 관리 API")
@RestController
@RequestMapping("/Admin/Refund")
@RequiredArgsConstructor
public class AdminRefundController {

    private final RefundService refundService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 환불 승인", description = "관리자가 특정 환불 요청을 승인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 승인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 처리된 환불)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @PutMapping("/admin/approve/{refundId}/{userId}")
    public ResponseEntity<RefundResponse> approveRefund(
            @PathVariable int refundId,
            @PathVariable int userId
    ) {
        RefundResponse response = refundService.approveRefund(refundId, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 환불 거절", description = "관리자가 특정 환불 요청을 거절합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 거절 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 처리된 환불)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @PutMapping("/admin/reject/{refundId}/{userId}")
    public ResponseEntity<RefundResponse> rejectRefund(
            @PathVariable int refundId,
            @PathVariable int userId
    ) {
        RefundResponse response = refundService.rejectRefund(refundId, userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 환불 완료 처리", description = "관리자가 환불 절차를 최종 완료 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 완료 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 승인되지 않은 환불)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @PutMapping("/admin/complete/{refundId}/{userId}")
    public ResponseEntity<RefundResponse> completeRefund(
            @PathVariable int refundId,
            @PathVariable int userId
    ) {
        RefundResponse response = refundService.completeRefund(refundId, userId);
        return ResponseEntity.ok(response);
    }

}
