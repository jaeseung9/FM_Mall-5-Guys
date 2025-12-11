package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryModifyRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 문의 API")
@RestController
@RequestMapping("/Inquiry")
@RequiredArgsConstructor
public class InquiryController extends BaseController {

    private final InquiryService inquiryService;

    @Operation(summary = "문의 단건 조회", description = "문의 ID로 특정 문의를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @GetMapping("/findOne/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> findInquiryById(@PathVariable int inquiryId) {
        InquiryResponseDTO resultInquiry = inquiryService.findInquiryByInquiryId(inquiryId);
        return ResponseEntity.ok(resultInquiry);
    }

    @Operation(summary = "상품별 문의 목록 조회", description = "특정 상품에 대한 모든 문의를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @GetMapping("/findByProduct/{productId}")
    public ResponseEntity<Page<InquiryResponseDTO>> findInquiryByProductId(@PathVariable int productId, @RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryResponseDTO> resultInquiry = inquiryService.findInquiryByProductIdSortedUpdatedAt(productId, curPage);
        return ResponseEntity.ok(resultInquiry);
    }

    @Operation(summary = "내 문의 목록 조회", description = "자신이 작성한 모든 문의를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<InquiryResponseDTO>> findMyInquiries(@RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryResponseDTO> resultInquiry = inquiryService.findInquiryByUserIdSortedUpdatedAt(getCurrentUserId(), curPage);
        return ResponseEntity.ok(resultInquiry);
    }

    @Operation(summary = "문의 등록", description = "새로운 상품 문의를 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "문의 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<InquiryResponseDTO> insertInquiry(@RequestBody InquiryRequestDTO requestDTO) {
        InquiryResponseDTO newInquiry = inquiryService.insertInquiry(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiry);
    }

    @Operation(summary = "내 문의 수정", description = "자신이 작성한 문의의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 답변 완료된 문의)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "문의 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @PutMapping("/modify/{inquiryId}")
    public ResponseEntity<InquiryResponseDTO> modifyInquiry(@PathVariable int inquiryId, @RequestBody InquiryModifyRequestDTO requestDTO) {
        InquiryResponseDTO updatedInquiry = inquiryService.modifyInquiryContent(inquiryId, getCurrentUserId(), requestDTO);
        return ResponseEntity.ok(updatedInquiry);
    }

    @Operation(summary = "내 문의 삭제", description = "자신이 작성한 문의를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "문의 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "문의 삭제 권한 없음"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(@PathVariable int inquiryId) {
        inquiryService.deleteInquiry(inquiryId, getCurrentUserId()); // userId를 함께 넘겨 권한 확인
        return ResponseEntity.noContent().build();
    }
}
