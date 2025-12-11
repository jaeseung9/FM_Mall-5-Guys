package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Brand;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.RowCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Integer> {
    List<Product> findByCategory(Category category);
    List<Product> findByRowCategory(RowCategory rowCategory);
    List<Product> findByBrand(Brand brand);

}
