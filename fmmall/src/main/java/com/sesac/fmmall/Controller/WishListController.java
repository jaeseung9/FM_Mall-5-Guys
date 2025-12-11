package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.WishList.WishListRequestDTO;
import com.sesac.fmmall.DTO.WishList.WishListResponseDTO;
import com.sesac.fmmall.Service.WishListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "위시리스트 API")
@RestController
@RequestMapping("/WishList")
@RequiredArgsConstructor
public class WishListController extends BaseController {

    private final WishListService wishListService;

    @Operation(summary = "내 위시리스트 단건 조회", description = "자신이 등록한 특정 위시리스트 항목을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
            @ApiResponse(responseCode = "404", description = "항목을 찾을 수 없음")
    })
    @GetMapping("/findOne/{wishListId}")
    public ResponseEntity<WishListResponseDTO> findMyWishListById(@PathVariable int wishListId) {
        WishListResponseDTO resultWishList = wishListService.findWishListByWishListId(wishListId, getCurrentUserId());
        return ResponseEntity.ok(resultWishList);
    }

    @Operation(summary = "내 위시리스트 목록 조회", description = "자신이 등록한 모든 위시리스트를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<WishListResponseDTO>> findMyWishLists(@RequestParam(defaultValue = "1") int curPage) {
        Page<WishListResponseDTO> resultWishList = wishListService.findWishListByUserIdSortedCreatedAt(getCurrentUserId(), curPage);
        return ResponseEntity.ok(resultWishList);
    }

    @Operation(summary = "위시리스트 토글", description = "상품을 위시리스트에 추가하거나 이미 있으면 제거합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토글 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "상품 또는 사용자를 찾을 수 없음")
    })
    @PostMapping("/toggle")
    public ResponseEntity<WishListResponseDTO> toggleWishlist(@RequestBody WishListRequestDTO request) {
        WishListResponseDTO toggleWishList = wishListService.toggleWishlist(getCurrentUserId(), request);
        return ResponseEntity.ok(toggleWishList);
    }

    @Operation(summary = "내 위시리스트 삭제", description = "자신이 등록한 특정 위시리스트 항목을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "항목을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{wishListId}")
    public ResponseEntity<Void> deleteMyWishList(@PathVariable int wishListId) {
        wishListService.deleteWishList(wishListId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}
