package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.BrandDTO;
import com.sesac.fmmall.Service.BrandService;
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

@Tag(name = "[관리자] 브랜드 관리 API")
@RestController
@RequestMapping("/Admin/Brand")
@RequiredArgsConstructor
public class AdminBrandController {

    private final BrandService brandService;

    /* ✅ 브랜드 전체 조회 (관리자 전용) */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 브랜드 전체 조회", description = "모든 브랜드 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "브랜드 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<BrandDTO>> adminFindAllBrands() {
        return ResponseEntity.ok(brandService.findAllBrands());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 브랜드 등록", description = "새로운 브랜드를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "브랜드 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<BrandDTO> AdminInsertBrand(@RequestBody @Valid BrandDTO brandDTO) {
        BrandDTO savedBrand = brandService.insertBrand(brandDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBrand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 브랜드 수정", description = "기존 브랜드의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "브랜드 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "브랜드를 찾을 수 없음")
    })
    @PutMapping("/modify/{brandId}")
    public ResponseEntity<BrandDTO> AdminModifyBrand(@PathVariable int brandId,
                                                @RequestBody @Valid BrandDTO brandDTO) {
        BrandDTO updatedBrand = brandService.modifyBrand(brandId, brandDTO);
        return ResponseEntity.ok(updatedBrand);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 브랜드 삭제", description = "브랜드를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "브랜드 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "브랜드를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{brandId}")
    public ResponseEntity<Void> AdminDeleteBrand(@PathVariable int brandId) {
        brandService.deleteBrand(brandId);
        return ResponseEntity.noContent().build();
    }
}
