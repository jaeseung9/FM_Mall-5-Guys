package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Order;
import com.sesac.fmmall.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {


    List<Order> findByUser(User user);


    List<Order> findByUser_UserId(Integer userId);


    //Order findByIdAndUser_UserId(Integer orderId, Integer userId);
}
