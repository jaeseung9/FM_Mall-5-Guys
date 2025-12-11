package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Order;
import com.sesac.fmmall.Entity.OrderItem;
import com.sesac.fmmall.Entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {


    List<OrderItem> findByOrder(Order order);


    //List<OrderItem> findByOrder_Id(Integer orderId);


    List<OrderItem> findByProduct(Product product);


    List<OrderItem> findByProduct_ProductId(Integer productId);
}