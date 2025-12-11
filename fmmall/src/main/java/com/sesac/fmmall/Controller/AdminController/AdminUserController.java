package com.sesac.fmmall.Controller.AdminController;

import com.sesac.fmmall.DTO.User.UserResponseDto;
import com.sesac.fmmall.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "[관리자] 사용자 관리 API")
@RestController
@RequestMapping("/Admin/User")
@RequiredArgsConstructor
public class AdminUserController {
    
    private final UserService userService;
    // private final JwtTokenProvider jwtTokenProvider; // 현재 이 컨트롤러에서는 직접 사용되지 않으므로 주석 처리 또는 제거 가능

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 모든 사용자 목록 조회", description = "시스템에 등록된 모든 사용자 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음")
    })
    @GetMapping("/findAll")
    public ResponseEntity<List<UserResponseDto>> adminFindAllUsers() {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 특정 사용자 조회", description = "해당 사용자의 정보를 조회합니다")
    @GetMapping("/findOne/{userId}")
    public ResponseEntity<UserResponseDto> adminFindOne(@PathVariable Integer userId) {
        UserResponseDto response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "[관리자] 사용자 삭제", description = "관리자 권한으로 특정 사용자를 비밀번호 확인 없이 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "사용자 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "관리자 권한 없음"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> adminDeleteUser(
            @PathVariable Integer userId) {
        userService.adminDeleteUser(userId); // UserService에 adminDeleteUser() 메소드가 있다고 가정
        return ResponseEntity.noContent().build();
    }
}
