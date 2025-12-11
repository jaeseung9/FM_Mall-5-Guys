package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Review.ReviewModifyRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewRequestDTO;
import com.sesac.fmmall.DTO.Review.ReviewResponseDTO;
import com.sesac.fmmall.Service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 API")
@RestController
@RequestMapping("/Review")
@RequiredArgsConstructor
public class ReviewController extends BaseController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID로 특정 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @GetMapping("/findOne/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> findReviewById(@PathVariable int reviewId) {
        ReviewResponseDTO resultReview = reviewService.findReviewByReviewId(reviewId);
        return ResponseEntity.ok(resultReview);
    }

    @Operation(summary = "주문 상품별 리뷰 조회", description = "특정 주문 상품에 대한 리뷰를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 조회 성공"),
            @ApiResponse(responseCode = "404", description = "주문 상품을 찾을 수 없음")
    })
    @GetMapping("/findByOrderItem/{orderItemId}")
    public ResponseEntity<ReviewResponseDTO> findReviewByOrderItemId(@PathVariable int orderItemId) {
        ReviewResponseDTO resultReview = reviewService.findReviewByOrderItemId(orderItemId);
        return ResponseEntity.ok(resultReview);
    }

    @Operation(summary = "내 리뷰 목록 조회", description = "자신이 작성한 모든 리뷰를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<ReviewResponseDTO>> findMyReviews(@RequestParam(defaultValue = "1") int curPage) {
        Page<ReviewResponseDTO> resultReview = reviewService.findReviewByUserIdSortedUpdatedAt(getCurrentUserId(), curPage);
        return ResponseEntity.ok(resultReview);
    }

    @Operation(summary = "리뷰 등록", description = "새로운 리뷰를 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "리뷰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "주문 상품을 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<ReviewResponseDTO> insertReview(@RequestBody ReviewRequestDTO requestDTO) {
        ReviewResponseDTO newReview = reviewService.insertReview(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newReview);
    }

    @Operation(summary = "내 리뷰 수정", description = "자신이 작성한 리뷰의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "리뷰 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @PutMapping("/modify/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> modifyReview(@PathVariable int reviewId, @RequestBody ReviewModifyRequestDTO requestDTO) {
        ReviewResponseDTO updatedReview = reviewService.modifyReviewContent(reviewId, getCurrentUserId(), requestDTO);
        return ResponseEntity.ok(updatedReview);
    }

    @Operation(summary = "내 리뷰 삭제", description = "자신이 작성한 리뷰를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "리뷰 삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "리뷰를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable int reviewId) {
        reviewService.deleteReview(reviewId, getCurrentUserId()); // userId를 함께 넘겨 권한 확인
        return ResponseEntity.noContent().build();
    }
}
