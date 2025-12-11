package com.sesac.fmmall.Controller;

import com.sesac.fmmall.DTO.User.*;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Service.UserService;
import com.sesac.fmmall.Security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "사용자 API")
@RestController
@RequestMapping("/User")
@RequiredArgsConstructor
public class UserController extends BaseController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 입력값 유효성 검사 실패)")
    })
    @PostMapping("/signup")
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody UserSaveRequestDto dto) {
        UserResponseDto response = userService.signup(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로그인", description = "사용자 로그인 후 토큰을 발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 아이디 또는 비밀번호 누락)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (아이디 또는 비밀번호 불일치)")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDto> login(@Valid @RequestBody LoginRequestDto dto) {
        User user = userService.login(dto.getLoginId(), dto.getPassword());
        String token = jwtTokenProvider.createToken(user);
        TokenResponseDto response = new TokenResponseDto(
                token,
                "Bearer",
                user.getLoginId(),
                user.getRole().name()
        );
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/findOne/{userId}")
//    public ResponseEntity<UserResponseDto> findOne(@PathVariable Integer userId) {
//        UserResponseDto response = userService.getUserInfo(userId);
//        return ResponseEntity.ok(response);
//    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인된 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/myFindOne")
    public ResponseEntity<UserResponseDto> myFind() {

        UserResponseDto response = userService.getUserInfo(getCurrentUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내 정보 수정", description = "현재 로그인된 사용자의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (예: 입력값 유효성 검사 실패)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (유효하지 않은 토큰)"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/modify")
    public ResponseEntity<UserResponseDto> modify(
            @RequestBody UserUpdateRequestDto dto) {

        UserResponseDto response = userService.updateUser(getCurrentUserId(), dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자의 계정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 누락"),
            @ApiResponse(responseCode = "401", description = "비밀번호 불일치 또는 인증 실패"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteUser(
            @RequestBody Map<String, String> request) {

        String password = request.get("password");
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("비밀번호를 입력해주세요.");
        }

        userService.deleteUser(getCurrentUserId(), password);

        Map<String, String> response = new HashMap<>();
        response.put("message", "회원탈퇴가 완료되었습니다.");
        return ResponseEntity.ok(response);
    }
}
