package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Payment.PaymentMethodResponseDto;
import com.sesac.fmmall.DTO.Payment.PaymentMethodSaveRequestDto;
import com.sesac.fmmall.Entity.PaymentMethod;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.PaymentMethodRepository;
import com.sesac.fmmall.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final UserRepository userRepository;

    public PaymentMethodResponseDto addPaymentMethod(Integer userId, PaymentMethodSaveRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        PaymentMethod paymentMethod = dto.toEntity(user);
        return new PaymentMethodResponseDto(paymentMethodRepository.save(paymentMethod));
    }

    @Transactional(readOnly = true)
    public List<PaymentMethodResponseDto> getPaymentMethodsByUserId(Integer userId) {
        List<PaymentMethod> paymentMethods = paymentMethodRepository.findByUser_UserId(userId);
        return paymentMethods.stream()
                .map(PaymentMethodResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PaymentMethodResponseDto getPaymentMethodById(Integer paymentMethodId, Integer userId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제수단입니다."));

        if (paymentMethod.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 결제수단만 조회할 수 있습니다.");
        }

        return new PaymentMethodResponseDto(paymentMethod);
    }

    public PaymentMethodResponseDto updatePaymentMethod(Integer paymentMethodId, Integer userId, PaymentMethodSaveRequestDto dto) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제수단입니다."));

        if (paymentMethod.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 결제수단만 수정할 수 있습니다.");
        }

        PaymentMethod updatedPaymentMethod = PaymentMethod.builder()
                .paymentMethodId(paymentMethod.getPaymentMethodId())
                .cardCompany(dto.getCardCompany())
                .maskedCardNumber(dto.getMaskedCardNumber())
                .isDefault(dto.getIsDefault())
                .createdAt(paymentMethod.getCreatedAt())
                .user(paymentMethod.getUser())
                .build();

        return new PaymentMethodResponseDto(paymentMethodRepository.save(updatedPaymentMethod));
    }

    public void deletePaymentMethod(Integer paymentMethodId, Integer userId) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 결제수단입니다."));

        if (paymentMethod.getUser().getUserId() != (userId)) {
            throw new IllegalArgumentException("본인의 결제수단만 삭제할 수 있습니다.");
        }

        paymentMethodRepository.delete(paymentMethod);
    }
}