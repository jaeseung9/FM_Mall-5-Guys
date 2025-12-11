package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.RowCategoryDTO;
import com.sesac.fmmall.Service.RowCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[관리자] 하위 카테고리 관리 API")
@RestController
@RequestMapping("/Admin/RowCategory")
@RequiredArgsConstructor
public class AdminRowCategoryController {

    private final RowCategoryService rowCategoryService;

    /* ✅ 상위 카테고리별 하위 카테고리 목록 조회 (관리자 전용) */
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 하위 카테고리 조회", description = "상위 카테고리 ID로 하위 카테고리 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "하위 카테고리 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "상위 카테고리를 찾을 수 없음")
    })
    @GetMapping("/findByCategoryId/{categoryId}")
    public ResponseEntity<List<RowCategoryDTO>> adminFindRowCategories(@PathVariable int categoryId) {
        List<RowCategoryDTO> rowCategories = rowCategoryService.findAllByCategoryId(categoryId);
        return ResponseEntity.ok(rowCategories);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 하위 카테고리 등록", description = "새로운 하위 카테고리를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "하위 카테고리 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<RowCategoryDTO> adminInsertRowCategory(@RequestBody RowCategoryDTO rowCategoryDTO) {
        RowCategoryDTO savedRowCategory = rowCategoryService.insertRowCategory(rowCategoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRowCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 하위 카테고리 수정", description = "기존 하위 카테고리의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "하위 카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "하위 카테고리를 찾을 수 없음")
    })
    @PutMapping("/modify/{rowCategoryId}")
    public ResponseEntity<RowCategoryDTO> adminModifyRowCategory(@PathVariable int rowCategoryId,
                                                            @RequestBody RowCategoryDTO rowCategoryDTO) {
        RowCategoryDTO updatedRowCategory = rowCategoryService.modifyRowCategory(rowCategoryId, rowCategoryDTO);
        return ResponseEntity.ok(updatedRowCategory);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 하위 카테고리 삭제", description = "하위 카테고리를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "하위 카테고리 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "하위 카테고리를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{rowCategoryId}")
    public ResponseEntity<Void> adminDeleteRowCategory(@PathVariable int rowCategoryId) {
        rowCategoryService.deleteRowCategory(rowCategoryId);
        return ResponseEntity.noContent().build();
    }
}
