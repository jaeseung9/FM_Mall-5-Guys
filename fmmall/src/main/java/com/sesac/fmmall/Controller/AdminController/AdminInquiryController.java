package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.Controller.BaseController;
import com.sesac.fmmall.DTO.Inquiry.InquiryResponseDTO;
import com.sesac.fmmall.Service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "[관리자] 상품 문의 관리 API")
@RestController
@RequestMapping("/Admin/Inquiry")
@RequiredArgsConstructor
public class AdminInquiryController extends BaseController {

    private final InquiryService inquiryService;

    @Operation(summary = "[관리자] 특정 사용자 문의 목록 조회", description = "관리자가 특정 사용자가 작성한 모든 문의를 최신순으로 페이징하여 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "문의 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findByUser/{userId}")
    public ResponseEntity<Page<InquiryResponseDTO>> findInquiryByUserId(@PathVariable int userId, @RequestParam(defaultValue = "1") int curPage) {
        Page<InquiryResponseDTO> resultInquiry = inquiryService.findInquiryByUserIdSortedUpdatedAt(userId, curPage);
        return ResponseEntity.ok(resultInquiry);
    }

    @Operation(summary = "[관리자] 문의 강제 삭제", description = "관리자가 ID로 특정 문의를 강제로 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "문의 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "문의를 찾을 수 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{inquiryId}")
    public ResponseEntity<Void> adminDeleteInquiry(@PathVariable int inquiryId) {
        inquiryService.deleteInquiry(inquiryId); // 서비스 메소드는 ID만으로 삭제
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "[관리자] 모든 문의 삭제", description = "관리자가 시스템의 모든 문의를 삭제합니다. (주의: 위험한 작업)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "모든 문의 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/deleteAll")
    public ResponseEntity<Void> adminDeleteAllInquiries() {
        inquiryService.deleteAllInquiry();
        return ResponseEntity.noContent().build();
    }
}
