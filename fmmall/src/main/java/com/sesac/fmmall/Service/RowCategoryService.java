package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.DTO.RowCategoryDTO;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.RowCategory;
import com.sesac.fmmall.Repository.CategoryRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.RowCategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RowCategoryService {
    private final RowCategoryRepository rowCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    /* 하위 카테고리 추가 */
    @Transactional
    public RowCategoryDTO insertRowCategory(RowCategoryDTO rowCategoryDTO) {
        // 상위 카테고리(ID)로 조회
        Category category = categoryRepository.findById(rowCategoryDTO.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리 입니다."));

        // DTO -> Entity 변환.
        RowCategory newRowCategory = RowCategory.builder()
                .name(rowCategoryDTO.getName())
                .category(category)
                .build();

        RowCategory savedRowCategory = rowCategoryRepository.save(newRowCategory);

        // Entity -> DTO 변환
        return modelMapper.map(savedRowCategory, RowCategoryDTO.class);
    }

    /* 하위 카테고리 네임 수정 */
    @Transactional
    public RowCategoryDTO modifyRowCategory(int rowCategoryId, RowCategoryDTO rowCategoryDTO) {

        RowCategory foundRowCategory = rowCategoryRepository.findById(rowCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 하위 카테고리 입니다."));

        foundRowCategory.setName(rowCategoryDTO.getName());

        return modelMapper.map(foundRowCategory, RowCategoryDTO.class);
    }

    /* 하위 카테고리 삭제 */
    @Transactional
    public void deleteRowCategory(int rowCategoryId) {
        if (!rowCategoryRepository.existsById(rowCategoryId)) {
            throw new IllegalArgumentException("삭제할 하위 카테고리 존재하지 않습니다.");
        }
        rowCategoryRepository.deleteById(rowCategoryId);
    }

    /* 하위 카테고리 상품 전체 조회 */
    @Transactional
    public List<ProductResponseDTO> findAllProductsByRowCategoryId(int rowCategoryId) {
        // 하위 카테고리 존재 여부 확인
        RowCategory foundRowCategory = rowCategoryRepository.findById(rowCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 하위 카테고리 입니다."));

        // 해당 하위 카테고리에 속한 상품들 조회.
        List<Product> products = productRepository.findByRowCategory(foundRowCategory);

        // Product -> ProductResponseDTO 변환
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .toList();
    }

    /* ✅ 상위 카테고리 ID로 하위 카테고리 목록 조회 (ADMIN용) */
    @Transactional
    public List<RowCategoryDTO> findAllByCategoryId(int categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리 입니다."));

        List<RowCategory> rowCategories = rowCategoryRepository.findByCategory(category);

        return rowCategories.stream()
                .map(rc -> RowCategoryDTO.builder()
                        .rowCategoryId(rc.getRowCategoryId())
                        .name(rc.getName())
                        .categoryId(rc.getCategory().getCategoryId())
                        .build())
                .toList();
    }
}
