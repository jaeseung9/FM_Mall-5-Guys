package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.Payment.PaymentMethodResponseDto;
import com.sesac.fmmall.Service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "[관리자] 결제 수단 관리 API")
@RestController
@RequestMapping("/Admin/Payment")
@RequiredArgsConstructor
public class AdminPaymentController {

    private final PaymentMethodService paymentMethodService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 결제 수단 목록 조회", description = "특정 사용자의 모든 결제 수단 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 수단 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<PaymentMethodResponseDto>> adminFindAll(@PathVariable Integer userId) {
        List<PaymentMethodResponseDto> responses = paymentMethodService.getPaymentMethodsByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 결제 수단 상세 조회", description = "특정 결제 수단의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 수단 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 결제 수단에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "결제 수단 또는 사용자를 찾을 수 없음")
    })
    @GetMapping("/findOne/{paymentMethodId}/{userId}")
    public ResponseEntity<PaymentMethodResponseDto> adminFindOne(
            @PathVariable Integer paymentMethodId,
            @PathVariable Integer userId) {
        PaymentMethodResponseDto response = paymentMethodService.getPaymentMethodById(paymentMethodId, userId);
        return ResponseEntity.ok(response);
    }
}
