package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.CategoryDTO;
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

@Tag(name = "[관리자] 상위카테고리 관리 API")
@RestController
@RequestMapping("/Admin/Category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 카테고리 등록", description = "새로운 상품 카테고리를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "카테고리 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)")
    })
    @PostMapping("/insert")
    public ResponseEntity<CategoryDTO> adminInsertCategory(@RequestBody @Valid CategoryDTO categoryDTO) {

        // 서비스 레이어에 DTO 전달해서 저장 로직 수행
        CategoryDTO savedCategory = categoryService.insertCategory(categoryDTO);

        // 생성된 카테고리 정보를 201 Created 로 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 카테고리 수정", description = "기존 카테고리의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @PutMapping("/modify/{categoryId}")
    public ResponseEntity<CategoryDTO> adminModifyCategory(@PathVariable int categoryId, @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updatedCategory = categoryService.modifyCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(updatedCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 카테고리 삭제", description = "카테고리를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{categoryId}")
    public ResponseEntity<Void> adminDeleteCategory(@PathVariable int categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
