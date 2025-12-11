package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.Product.ProductRequestDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] 상품 관리 API")
@RestController
@RequestMapping("/Admin/Product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품 등록", description = "관리자가 새로운 상품을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "상품 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<ProductResponseDTO> adminInsertProduct(@RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO newProduct = productService.createProduct(productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품 수정", description = "관리자가 기존 상품의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PutMapping("/modify/{productId}")
    public ResponseEntity<ProductResponseDTO> adminModifyProduct(@PathVariable int productId, @RequestBody ProductRequestDTO productRequestDTO){
        ProductResponseDTO updateProduct = productService.modifyProduct(productId, productRequestDTO);
        return ResponseEntity.ok(updateProduct);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 상품 삭제", description = "관리자가 상품을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<Void> adminDeleteProduct(@PathVariable int productId){
        productService.deleteProduct(productId);

        return ResponseEntity.ok().build();
    }
}
