package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {

    Optional<Cart> findByUser_UserId(int userId);
}
