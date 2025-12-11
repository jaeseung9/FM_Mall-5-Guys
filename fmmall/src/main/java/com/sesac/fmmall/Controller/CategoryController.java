package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.CategoryDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Service.CategoryService;
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

@Tag(name = "카테고리 API")
@RestController
@RequestMapping("/Category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

//    @Operation(summary = "[관리자] 카테고리 등록", description = "새로운 상품 카테고리를 등록합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "201", description = "카테고리 등록 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)")
//    })
//    @PostMapping("/insert")
//    public ResponseEntity<CategoryDTO> insertCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
//
//        // 서비스 레이어에 DTO 전달해서 저장 로직 수행
//        CategoryDTO savedCategory = categoryService.insertCategory(categoryDTO);
//
//        // 생성된 카테고리 정보를 201 Created 로 반환
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(savedCategory);
//    }
//
//    @Operation(summary = "[관리자] 카테고리 수정", description = "기존 카테고리의 정보를 수정합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
//            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
//            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
//    })
//    @PutMapping("/modify/{categoryId}")
//    public ResponseEntity<CategoryDTO> modifyCategory(@PathVariable int categoryId, @RequestBody CategoryDTO categoryDTO) {
//        CategoryDTO updatedCategory = categoryService.modifyCategory(categoryId, categoryDTO);
//        return ResponseEntity.ok(updatedCategory);
//    }
//
//    @Operation(summary = "[관리자] 카테고리 삭제", description = "카테고리를 삭제합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
//    })
//    @DeleteMapping("/delete/{categoryId}")
//    public ResponseEntity<Void> deleteCategory(@PathVariable int categoryId) {
//        categoryService.deleteCategory(categoryId);
//        return ResponseEntity.noContent().build();
//    }

    @Operation(summary = "전체 카테고리 조회", description = "모든 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<CategoryDTO>> findAllCategories() {
        List<CategoryDTO> categories = categoryService.findAllCategories();
        return ResponseEntity.ok(categories);
    }


    @Operation(summary = "카테고리별 상품 목록 조회", description = "특정 카테고리에 속한 모든 상품 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상품 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @GetMapping("/findAll/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getAllProductsByCategory(@PathVariable int categoryId) {
        List<ProductResponseDTO> products = categoryService.findAllProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }


}
