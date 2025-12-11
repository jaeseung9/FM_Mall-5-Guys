package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.Controller.BaseController;
import com.sesac.fmmall.DTO.WishList.WishListResponseDTO;
import com.sesac.fmmall.Service.WishListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "[관리자] 위시리스트 관리 API")
@RestController
@RequestMapping("/Admin/WishList")
@RequiredArgsConstructor
public class AdminWishListController extends BaseController {

    private final WishListService wishListService;

    @Operation(summary = "[관리자] 모든 위시리스트 조회", description = "시스템의 모든 위시리스트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findAll")
    public ResponseEntity<List<WishListResponseDTO>> findAll() {
        List<WishListResponseDTO> resultWishList = wishListService.findAllWishList();
        return ResponseEntity.ok(resultWishList);
    }

    @Operation(summary = "[관리자] 모든 위시리스트 삭제", description = "시스템의 모든 위시리스트를 삭제합니다. (주의: 위험한 작업)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllWishList() {
        wishListService.deleteAllWishList();
        return ResponseEntity.noContent().build();
    }
}
