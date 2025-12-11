package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Integer> {
}
