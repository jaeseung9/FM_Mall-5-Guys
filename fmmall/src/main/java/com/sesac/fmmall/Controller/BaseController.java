package com.sesac.fmmall.Controller;

import com.sesac.fmmall.Security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {

    protected int getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("인증 정보가 없습니다.");
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser().getUserId();
        }

        // 혹시나 User 그대로 들어온 경우 대비 (안 쓰일 가능성 높음)
        if (principal instanceof com.sesac.fmmall.Entity.User user) {
            return user.getUserId();
        }

        // 예전 방식(정수) 남아 있을 수도 있으니 한 번 더 배려
        if (principal instanceof Integer id) {
            return id;
        }

        throw new IllegalStateException("지원하지 않는 principal 타입: " + principal.getClass());
    }
}
