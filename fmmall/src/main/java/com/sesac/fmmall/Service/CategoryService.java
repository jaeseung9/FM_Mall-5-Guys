package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.CategoryDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Repository.CategoryRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    /* 추가할 상위 카테고리를 입력 후 , 상위 카테고리를 추가/등록 */
    @Transactional
    public CategoryDTO insertCategory(CategoryDTO categoryDTO) {

        // DTO -> Entity 변환.
        Category newCategory = Category.builder()
                .name(categoryDTO.getCategoryName())
                .build();

        // 내부적으로 EntityManager.persist( ) 호출되어 영속성 컨텍스트로 들어간다.
        Category savedCategory = categoryRepository.save(newCategory);

        // 저장 후 생성된 Entity를 다시 DTO로 변환하여 반환.
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    /* categoryId값을 넘겨, 해당 상위 카테고리 정보 수정 진행. */
    @Transactional
    public CategoryDTO modifyCategory(int categoryId, CategoryDTO categoryDTO) {
        Category foundCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리입니다."));

        foundCategory.modify(
                categoryDTO.getCategoryName()
        );

        return modelMapper.map(foundCategory, CategoryDTO.class);
    }


    /* categoryId값을 넘겨, 해당 상위 카테고리 정보 삭제 진행 */
    @Transactional
    public void deleteCategory(int categoryId) {
        if(!categoryRepository.existsById(categoryId)) {
            throw new IllegalArgumentException("삭제할 상위 카테고리가 존재하지 않습니다.");
        }
        categoryRepository.deleteById(categoryId);
    }

    /* categoryId값을 넘겨 해당 상위카테고리의 전체 상품 목록 조회. */
    @Transactional
    public List<ProductResponseDTO> findAllProductsByCategoryId(int categoryId) {
        // 1. 카테고리 존재 여부 확인.
        Category foundCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리 입니다."));

        // 2. 해당 상위 카테고리에 속한 상품들 조회.
        List<Product> products = productRepository.findByCategory(foundCategory);

        // 3. Product를 ProductResponseDTO로 변환.
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .toList();
    }

    public List<CategoryDTO> findAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .collect(Collectors.toList());
    }
}
