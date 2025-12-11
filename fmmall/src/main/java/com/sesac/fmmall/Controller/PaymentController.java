package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Payment.*;
import com.sesac.fmmall.Service.PaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "결제 수단 API")
@RestController
@RequestMapping("/Payment")
@RequiredArgsConstructor
public class PaymentController extends BaseController {

    private final PaymentMethodService paymentMethodService;

    @Operation(summary = "결제 수단 등록", description = "특정 사용자의 결제 수단(카드)을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "결제 수단 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 입력값 유효성 검사 실패)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<PaymentMethodResponseDto> insert(
            @Valid @RequestBody PaymentMethodSaveRequestDto dto) {
        PaymentMethodResponseDto response = paymentMethodService.addPaymentMethod(getCurrentUserId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "결제 수단 목록 조회", description = "특정 사용자의 모든 결제 수단 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 수단 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<PaymentMethodResponseDto>> findAll() {
        List<PaymentMethodResponseDto> responses = paymentMethodService.getPaymentMethodsByUserId(getCurrentUserId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "결제 수단 상세 조회", description = "특정 결제 수단의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 수단 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 결제 수단에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "결제 수단 또는 사용자를 찾을 수 없음")
    })
    @GetMapping("/findOne/{paymentMethodId}")
    public ResponseEntity<PaymentMethodResponseDto> findOne(
            @PathVariable Integer paymentMethodId) {
        PaymentMethodResponseDto response = paymentMethodService.getPaymentMethodById(paymentMethodId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 수단 수정", description = "특정 결제 수단의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "결제 수단 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 입력값 유효성 검사 실패)"),
            @ApiResponse(responseCode = "403", description = "해당 결제 수단에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "결제 수단 또는 사용자를 찾을 수 없음")
    })
    @PutMapping("/modify/{paymentMethodId}")
    public ResponseEntity<PaymentMethodResponseDto> modify(
            @PathVariable Integer paymentMethodId,
            @Valid @RequestBody PaymentMethodSaveRequestDto dto) {
        PaymentMethodResponseDto response = paymentMethodService.updatePaymentMethod(paymentMethodId, getCurrentUserId(), dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "결제 수단 삭제", description = "특정 결제 수단을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "결제 수단 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "해당 결제 수단에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "결제 수단 또는 사용자를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{paymentMethodId}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer paymentMethodId) {
        paymentMethodService.deletePaymentMethod(paymentMethodId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
