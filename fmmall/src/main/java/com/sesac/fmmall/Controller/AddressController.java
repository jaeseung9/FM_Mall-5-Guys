package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Address.*;
import com.sesac.fmmall.Service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배송지 주소 API")
@RestController
@RequestMapping("/Address")
@RequiredArgsConstructor
public class AddressController extends BaseController{

    private final AddressService addressService;

    @Operation(summary = "내 배송지 등록", description = "현재 로그인된 사용자의 배송지를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송지 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 입력값 유효성 검사 실패)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<AddressResponseDto> insert(
            @Valid @RequestBody AddressSaveRequestDto dto) {
        AddressResponseDto response = addressService.addAddress(getCurrentUserId(), dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 배송지 목록 조회", description = "현재 로그인된 사용자의 모든 배송지 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<AddressResponseDto>> findAll() {
        List<AddressResponseDto> responses = addressService.getAddressesByUserId(getCurrentUserId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "내 배송지 상세 조회", description = "특정 배송지의 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "해당 배송지에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송지 또는 사용자를 찾을 수 없음")
    })
    @GetMapping("/findOne/{addressId}")
    public ResponseEntity<AddressResponseDto> findOne(@PathVariable Integer addressId) {
        AddressResponseDto response = addressService.getAddressById(addressId, getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 배송지 수정", description = "특정 배송지의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 입력값 유효성 검사 실패)"),
            @ApiResponse(responseCode = "403", description = "해당 배송지에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송지 또는 사용자를 찾을 수 없음")
    })
    @PutMapping("/modify/{addressId}")
    public ResponseEntity<AddressResponseDto> modify(
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressSaveRequestDto dto) {
        AddressResponseDto response = addressService.updateAddress(addressId, getCurrentUserId(), dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 배송지 삭제", description = "특정 배송지를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "배송지 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "해당 배송지에 대한 접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송지 또는 사용자를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Integer addressId) {
        addressService.deleteAddress(addressId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
