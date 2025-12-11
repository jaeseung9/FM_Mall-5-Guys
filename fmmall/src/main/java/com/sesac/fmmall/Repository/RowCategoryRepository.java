package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.RowCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RowCategoryRepository extends JpaRepository<RowCategory,Integer> {
    // 상위 카테고리 기준으로 하위 카테고리 목록 조회
    List<RowCategory> findByCategory(Category category);
}
