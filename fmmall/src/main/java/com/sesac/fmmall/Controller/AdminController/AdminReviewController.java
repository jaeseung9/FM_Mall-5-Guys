package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.Controller.BaseController;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] 리뷰 관리 API")
@RestController
@RequestMapping("/Admin/Review")
@RequiredArgsConstructor
public class AdminReviewController extends BaseController {

    private final ReviewService reviewService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 특정 사용자 리뷰 목록 조회", description = "관리자가 특정 사용자가 작성한 모든 리뷰를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<Page<ReviewResponseDTO>> findReviewByUserId(@PathVariable int userId, @RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultReview = reviewService.findReviewByUserIdSortedUpdatedAt(userId, curPage);
        return ResponseEntity.ok(resultReview);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 리뷰 강제 삭제", description = "관리자가 ID로 특정 리뷰를 강제로 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> adminDeleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId); // 서비스 메소드는 ID만으로 삭제
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 모든 리뷰 삭제", description = "관리자가 시스템의 모든 리뷰를 삭제합니다. (주의: 위험한 작업)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "모든 리뷰 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> adminDeleteAllReviews() {
        reviewService.deleteAllReview();
        return ResponseEntity.noContent().build();
    }
}
