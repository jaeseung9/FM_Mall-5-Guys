package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.Address.AddressResponseDto;
import com.sesac.fmmall.Service.AddressService;
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

@Tag(name = "[관리자] 주소 관리 API")
@RestController
@RequestMapping("/Admin/Address")
@RequiredArgsConstructor
public class AdminAddressController {

    private final AddressService addressService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 특정 사용자 배송지 목록 조회", description = "관리자가 특정 사용자의 모든 배송지 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findAll/{userId}")
    public ResponseEntity<List<AddressResponseDto>> adminFindAll(@PathVariable Integer userId) {
        List<AddressResponseDto> responses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 특정 사용자 배송지 상세 조회", description = "관리자가 특정 사용자의 특정 배송지 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 상세 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "배송지 또는 사용자를 찾을 수 없음")
    })
    @GetMapping("/findOne/{addressId}/{userId}")
    public ResponseEntity<AddressResponseDto> adminFindOne(
            @PathVariable Integer addressId,
            @PathVariable Integer userId) {
        AddressResponseDto response = addressService.getAddressById(addressId, userId);
        return ResponseEntity.ok(response);
    }

}
