package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentMethodRepository extends JpaRepository<PaymentMethod,Integer> {

    List<PaymentMethod> findByUser_UserId(Integer userId);

    Optional<PaymentMethod> findByUser_UserIdAndIsDefault(Integer userId, Boolean isDefault);
}
