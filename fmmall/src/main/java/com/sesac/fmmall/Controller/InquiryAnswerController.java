package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.Service.InquiryAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "문의 답변 API")
@RestController
@RequestMapping("/InquiryAnswer")
@RequiredArgsConstructor
public class InquiryAnswerController extends BaseController {

    private final InquiryAnswerService inquiryAnswerService;

    @Operation(summary = "문의 답변 조회", description = "문의 답변 ID로 특정 답변을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 조회 성공"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    @GetMapping("/findOne/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> findInquiryAnswerById(@PathVariable int inquiryAnswerId) {
        InquiryAnswerResponseDTO resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByInquiryAnswerId(inquiryAnswerId);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @Operation(summary = "문의별 답변 목록 조회", description = "특정 상품 문의에 달린 모든 답변을 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @GetMapping("/findByInquiry/{inquiryId}")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByInquiryId(
            @PathVariable int inquiryId,
            @RequestParam(defaultValue = "1") int curPage
    ) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByInquiryIdSortedUpdatedAt(inquiryId, curPage);
        return ResponseEntity.ok(resultInquiryAnswer);
    }
}
