package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Order;
import com.sesac.fmmall.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {


    Optional<Payment> findByOrder(Order order);


    //Optional<Payment> findByOrder_Id(Integer orderId);
}