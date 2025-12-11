package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.User.UserResponseDto;
import com.sesac.fmmall.DTO.User.UserSaveRequestDto;
import com.sesac.fmmall.DTO.User.UserUpdateRequestDto;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDto signup(UserSaveRequestDto dto) {
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 로그인 아이디입니다.");
        }

        User user = dto.toEntity();
        user.encodePassword(passwordEncoder.encode(user.getPassword()));

        return new UserResponseDto(userRepository.save(user));
    }

    public User login(String loginId, String rawPassword) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        return new UserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());
    }

    public UserResponseDto updateUser(Integer userId, UserUpdateRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (dto.getUserName() != null) {
            user = User.builder()
                    .userId(user.getUserId())
                    .loginId(user.getLoginId())
                    .password(user.getPassword())
                    .userName(dto.getUserName())
                    .userPhone(dto.getUserPhone() != null ? dto.getUserPhone() : user.getUserPhone())
                    .createdAt(user.getCreatedAt())
                    .role(user.getRole())
                    .build();
        }

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.encodePassword(passwordEncoder.encode(dto.getPassword()));
        }

        return new UserResponseDto(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Integer userId, String password) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

     /*   List<Order> orders = orderRepository.findByUser_UserId(userId);
        if (!orders.isEmpty()) {
            throw new IllegalStateException("주문 내역이 있는 경우 탈퇴할 수 없습니다. 고객센터에 문의해주세요.");
        }*/

        userRepository.delete(user);
    }

    @Transactional
    public void adminDeleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userRepository.delete(user);
    }
}
