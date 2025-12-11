package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Refund.RefundCreateRequest;
import com.sesac.fmmall.DTO.Refund.RefundResponse;
import com.sesac.fmmall.DTO.Refund.RefundSummaryResponse;
import com.sesac.fmmall.Service.RefundService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "환불 API")
@RestController
@RequestMapping("/Refund")
@RequiredArgsConstructor
public class RefundController extends BaseController {

    private final RefundService refundService;

    @Operation(summary = "환불 신청", description = "사용자가 특정 주문 항목에 대한 환불을 신청합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "환불 신청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 환불 처리된 주문)"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 주문 항목을 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<RefundResponse> insertRefund(
            @RequestBody RefundCreateRequest request
    ) {
        RefundResponse response = refundService.createRefund(getCurrentUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 환불 내역 전체 조회", description = "현재 로그인된 사용자의 모든 환불 내역을 요약하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<RefundSummaryResponse>> findAllByUser() {
        List<RefundSummaryResponse> responses = refundService.getRefundsByUser(getCurrentUserId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "상품별 내 환불 내역 조회", description = "특정 상품에 대한 현재 사용자의 모든 환불 내역을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 내역 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 또는 상품을 찾을 수 없음")
    })
    @GetMapping("/findByProduct/{productId}")
    public ResponseEntity<List<RefundResponse>> findByProduct(
            @PathVariable int productId
    ) {
        List<RefundResponse> responses = refundService.getRefundsByUserAndProduct(getCurrentUserId(), productId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "환불 상세 조회", description = "환불 ID로 특정 환불 건의 상세 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 환불에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @GetMapping("/findOne/{refundId}")
    public ResponseEntity<RefundResponse> findOne(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.getRefundDetail(refundId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 환불 승인", description = "관리자가 특정 환불 요청을 승인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 승인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 처리된 환불)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @PutMapping("/admin/approve/{refundId}")
    public ResponseEntity<RefundResponse> approveRefund(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.approveRefund(refundId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 환불 거절", description = "관리자가 특정 환불 요청을 거절합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 거절 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 처리된 환불)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @PutMapping("/admin/reject/{refundId}")
    public ResponseEntity<RefundResponse> rejectRefund(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.rejectRefund(refundId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "[관리자] 환불 완료 처리", description = "관리자가 환불 절차를 최종 완료 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "환불 완료 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 승인되지 않은 환불)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "환불 정보를 찾을 수 없음")
    })
    @PutMapping("/admin/complete/{refundId}")
    public ResponseEntity<RefundResponse> completeRefund(
            @PathVariable int refundId
    ) {
        RefundResponse response = refundService.completeRefund(refundId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }
}
