package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.Controller.BaseController;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerModifyRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerRequestDTO;
import com.sesac.fmmall.DTO.Inquiry.InquiryAnswerResponseDTO;
import com.sesac.fmmall.Service.InquiryAnswerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] 문의 답변 관리 API")
@RestController
@RequestMapping("/Admin/InquiryAnswer")
@RequiredArgsConstructor
public class AdminInquiryAnswerController extends BaseController {

    private final InquiryAnswerService inquiryAnswerService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 특정 관리자 답변 목록 조회", description = "관리자가 특정 관리자(사용자)가 작성한 모든 답변을 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findInquiryAnswerByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "1") int curPage
    ) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByUserIdSortedUpdatedAt(userId, curPage);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 내 답변 목록 조회", description = "현재 로그인한 관리자가 작성한 모든 답변을 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/findByUser/me")
    public ResponseEntity<Page<InquiryAnswerResponseDTO>> findMyInquiryAnswers(
            @RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryAnswerResponseDTO> resultInquiryAnswer = inquiryAnswerService.findInquiryAnswerByUserIdSortedUpdatedAt(getCurrentUserId(), curPage);
        return ResponseEntity.ok(resultInquiryAnswer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 문의 답변 등록", description = "관리자가 상품 문의에 대한 답변을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 이미 답변 완료된 문의)"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "문의 또는 사용자를 찾을 수 없음")
    })
    @PostMapping("/insert")
    public ResponseEntity<InquiryAnswerResponseDTO> insertInquiryAnswer(@RequestBody InquiryAnswerRequestDTO requestDTO) {
        InquiryAnswerResponseDTO newInquiryAnswer = inquiryAnswerService.insertInquiryAnswer(getCurrentUserId(), requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(newInquiryAnswer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 문의 답변 수정", description = "관리자가 자신이 작성한 답변의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 필수 필드 누락)"),
            @ApiResponse(responseCode = "403", description = "답변 수정 권한 없음"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    @PutMapping("/modify/{inquiryAnswerId}")
    public ResponseEntity<InquiryAnswerResponseDTO> modifyInquiryAnswer(
            @PathVariable int inquiryAnswerId,
            @RequestBody InquiryAnswerModifyRequestDTO requestDTO
    ) {
        InquiryAnswerResponseDTO updatedInquiryAnswer = inquiryAnswerService.modifyInquiryAnswerContent(inquiryAnswerId, getCurrentUserId(), requestDTO);
        return ResponseEntity.ok(updatedInquiryAnswer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 문의 답변 삭제", description = "관리자가 답변을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "답변 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "답변을 찾을 수 없음")
    })
    @DeleteMapping("/delete/{inquiryAnswerId}")
    public ResponseEntity<Void> deleteInquiryAnswer(@PathVariable int inquiryAnswerId) {
        inquiryAnswerService.deleteInquiryAnswer(inquiryAnswerId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 모든 문의 답변 삭제", description = "관리자가 시스템의 모든 문의 답변을 삭제합니다. (주의: 위험한 작업)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "모든 답변 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> deleteAllInquiryAnswer() {
        inquiryAnswerService.deleteAllInquiryAnswer();
        return ResponseEntity.noContent().build();
    }
}
