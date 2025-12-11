package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.CategoryDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Repository.CategoryRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // SecurityConfig 때문에 필요한 MockBean 들 (ProductServiceTest 와 동일 패턴)
    @MockBean
    private PasswordEncoder passwordEncoder;

    // Service 에서 사용하는 ModelMapper 도 MockBean 으로 등록
    @MockBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("insertCategory - 상위 카테고리 등록 시 DB에 저장되고 DTO가 반환된다.")
    void insertCategory_success() {
        // 🔹 1) 요청 DTO 준비
        CategoryDTO requestDTO = CategoryDTO.builder()
                .categoryName("테스트 상위 카테고리")
                .build();

        // 🔹 2) ModelMapper mock 이 실제 매핑을 하도록 설정
        given(modelMapper.map(any(Category.class), eq(CategoryDTO.class)))
                .willAnswer(invocation -> {
                    Category source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    // Category.name -> CategoryDTO.categoryName 매핑
                    real.typeMap(Category.class, CategoryDTO.class)
                            .addMappings(m -> {
                                m.map(Category::getCategoryId, CategoryDTO::setCategoryId);
                                m.map(Category::getName, CategoryDTO::setCategoryName);
                            });

                    return real.map(source, CategoryDTO.class);
                });

        // 🔹 3) 서비스 호출
        CategoryDTO result = categoryService.insertCategory(requestDTO);

        System.out.println("=== 🔥 DTO로 반환된 결과 ===");
        System.out.println(result.getCategoryId() + " / " + result.getCategoryName());

        // 🔹 4) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId())
                .as("DB에 저장되면서 category_id 가 생성되어야 합니다.")
                .isGreaterThan(0);
        assertThat(result.getCategoryName()).isEqualTo("테스트 상위 카테고리");

        // 🔹 5) 실제 DB에 제대로 들어갔는지 검증
        Optional<Category> optionalCategory = categoryRepository.findById(result.getCategoryId());
        assertThat(optionalCategory)
                .as("반환된 categoryId 로 DB에서 조회가 되어야 합니다.")
                .isPresent();

        Category saved = optionalCategory.get();

        System.out.println("=== 🔥 DB에서 다시 읽어온 Entity ===");
        System.out.println(saved.getCategoryId() + " / " + saved.getName());

        assertThat(saved.getName()).isEqualTo("테스트 상위 카테고리");
    }

    @Test
    @DisplayName("modifyCategory - 기존 상위 카테고리 수정 시 변경 내용이 반영된다.")
    void modifyCategory_success() {
        // 🔹 1) 수정 대상 카테고리 하나 선택 (DB에 최소 1개 있다고 가정)
        List<Category> all = categoryRepository.findAll();
        assertThat(all)
                .as("수정 테스트를 위해 최소 1개 이상의 상위 카테고리 데이터가 필요합니다.")
                .isNotEmpty();

        Category original = all.get(3);
        int categoryId = original.getCategoryId();

        String updatedName = original.getName() + "_수정";

        // 🔹 2) 요청 DTO 생성
        CategoryDTO requestDTO = CategoryDTO.builder()
                .categoryName(updatedName)
                .build();

        // 🔹 3) ModelMapper mock → 실제 매핑 수행하도록 설정
        given(modelMapper.map(any(Category.class), eq(CategoryDTO.class)))
                .willAnswer(invocation -> {
                    Category source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    real.typeMap(Category.class, CategoryDTO.class)
                            .addMappings(m -> {
                                m.map(Category::getCategoryId, CategoryDTO::setCategoryId);
                                m.map(Category::getName, CategoryDTO::setCategoryName);
                            });

                    return real.map(source, CategoryDTO.class);
                });

        // 🔹 4) 서비스 호출
        CategoryDTO result = categoryService.modifyCategory(categoryId, requestDTO);

        // 🔹 5) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getCategoryId()).isEqualTo(categoryId);
        assertThat(result.getCategoryName()).isEqualTo(updatedName);

        // 🔹 6) 실제 DB에 반영됐는지 확인
        Category updated = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new AssertionError("수정된 카테고리가 DB에 존재하지 않습니다."));

        assertThat(updated.getName()).isEqualTo(updatedName);

        // 🔹 7) updated_at 이 DB에서 잘 갱신되는지(DDL 설정이 되어 있다면) 확인하고 싶다면:
        // assertThat(updated.getUpdatedAt()).isNotNull();
    }

    @Test
    @Transactional      // 🔥 테스트 안에서만 트랜잭션
    @Rollback           // 🔥 테스트 끝나면 롤백 (실제 DB 내용 원상 복구)
    @DisplayName("deleteCategory - 기존에 존재하는 상위 카테고리가 삭제된다.")
    void deleteCategory_success_withExistingCategory() {
        // 🔹 1) DB에 이미 있는 카테고리들 조회
        List<Category> all = categoryRepository.findAll();
        assertThat(all)
                .as("기존 상위 카테고리 데이터가 최소 1개 이상 있어야 합니다.")
                .isNotEmpty();

        // 너무 중요한 데이터 피하고 싶으면 맨 처음/맨 마지막 등 하나 고르면 됨
        Category target = all.get(3);
        int categoryId = target.getCategoryId();

        System.out.println("삭제 대상 카테고리 ID = " + categoryId + ", name = " + target.getName());

        // 🔹 2) 삭제 전에는 존재해야 함
        assertThat(categoryRepository.existsById(categoryId)).isTrue();

        // 🔹 3) 서비스 메서드 호출 (실제 삭제 실행)
        categoryService.deleteCategory(categoryId);

        // 🔹 4) 같은 트랜잭션 안에서는 삭제 결과가 보인다.
        assertThat(categoryRepository.existsById(categoryId)).isFalse();
    }

    @Test
    @Transactional
    @Rollback
    @DisplayName("findProductsByCategoryId - 상위 카테고리 기준 상품 목록을 조회하고 콘솔에 출력한다.")
    void findProductsByCategoryId_printProducts() {
        // 🔹 1) 카테고리 하나 선택
        List<Category> allCategories = categoryRepository.findAll();
        assertThat(allCategories)
                .as("테스트용 상위 카테고리가 최소 1개 이상 있어야 합니다.")
                .isNotEmpty();

        Category category = allCategories.get(0);
        int categoryId = category.getCategoryId();

        // 🔹 2) 이 카테고리에 실제 상품들이 있는지 확인 (없어도 테스트는 되지만, 보기 좋게)
        List<Product> products = productRepository.findByCategory(category);
        assertThat(products)
                .as("테스트용으로 선택한 카테고리에 상품이 최소 1개 이상 있으면 콘솔 확인이 더 쉽습니다.")
                .isNotEmpty();

        // 🔹 3) ModelMapper mock → Product -> ProductResponseDTO 매핑 설정
        given(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .willAnswer(invocation -> {
                    Product source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    // name -> productName 매핑 (필드명 다를 경우)
                    real.typeMap(Product.class, ProductResponseDTO.class)
                            .addMappings(m -> m.map(Product::getName, ProductResponseDTO::setProductName));

                    return real.map(source, ProductResponseDTO.class);
                });

        // 🔹 4) 서비스 호출
        List<ProductResponseDTO> result = categoryService.findAllProductsByCategoryId(categoryId);

        // 🔹 5) 검증
        assertThat(result).isNotEmpty();

        // 🔹 6) 실제 데이터 콘솔에 보기 좋게 출력
        System.out.println("=== 🔥 카테고리 ID = " + categoryId + " 의 상품 목록 ===");
        for (ProductResponseDTO dto : result) {
            System.out.printf("상품ID=%d / 이름=%s / 가격=%d%n",
                    dto.getProductId(),
                    dto.getProductName(),
                    dto.getProductPrice());
        }
    }
}
