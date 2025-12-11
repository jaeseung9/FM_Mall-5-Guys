package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.RowCategoryDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.RowCategory;
import com.sesac.fmmall.Repository.CategoryRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.RowCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class RowCategoryServiceTest {

    @Autowired
    private RowCategoryService rowCategoryService;

    @Autowired
    private RowCategoryRepository rowCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // Service에서 사용하는 ModelMapper를 MockBean으로 주입
    @MockBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("insertRowCategory - 하위 카테고리 등록 시 DB에 저장되고 DTO가 반환된다.")
    void insertRowCategory_success() {
        // 🔹 1) 상위 카테고리 하나 확보 (DB에 최소 1개 있다고 가정)
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories)
                .as("하위 카테고리 등록 테스트를 위해 최소 1개 이상의 상위 카테고리 데이터가 필요합니다.")
                .isNotEmpty();

        Category parentCategory = categories.get(0);

        // 🔹 2) 요청 DTO 생성
        RowCategoryDTO requestDTO = RowCategoryDTO.builder()
                .name("테스트 하위 카테고리")
                .categoryId(parentCategory.getCategoryId())
                .build();

        // 🔹 3) ModelMapper mock -> 실제 매핑 수행하도록 설정 (RowCategory -> RowCategoryDTO)
        given(modelMapper.map(any(RowCategory.class), eq(RowCategoryDTO.class)))
                .willAnswer(invocation -> {
                    RowCategory source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    real.typeMap(RowCategory.class, RowCategoryDTO.class)
                            .addMappings(m -> {
                                m.map(RowCategory::getRowCategoryId, RowCategoryDTO::setRowCategoryId);
                                m.map(RowCategory::getName, RowCategoryDTO::setName);
                                m.map(rc -> rc.getCategory().getCategoryId(), RowCategoryDTO::setCategoryId);
                            });

                    return real.map(source, RowCategoryDTO.class);
                });

        // 🔹 4) 서비스 호출
        RowCategoryDTO result = rowCategoryService.insertRowCategory(requestDTO);

        System.out.println("=== 🔥 insertRowCategory 결과 DTO ===");
        System.out.println(result.getRowCategoryId() + " / " + result.getName() +
                " / parentId=" + result.getCategoryId());

        // 🔹 5) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getRowCategoryId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("테스트 하위 카테고리");
        assertThat(result.getCategoryId()).isEqualTo(parentCategory.getCategoryId());

        // 🔹 6) 실제 DB 검증
        RowCategory saved = rowCategoryRepository.findById(result.getRowCategoryId())
                .orElseThrow(() -> new AssertionError("생성된 하위 카테고리가 DB에 존재하지 않습니다."));

        assertThat(saved.getName()).isEqualTo("테스트 하위 카테고리");
        assertThat(saved.getCategory().getCategoryId()).isEqualTo(parentCategory.getCategoryId());
    }

    @Test
    @DisplayName("modifyRowCategory - 기존 하위 카테고리 수정 시 변경 내용이 반영되어 DTO로 반환된다.")
    void modifyRowCategory_success() {
        // 🔹 1) 수정 대상 하위 카테고리 하나 선택
        List<RowCategory> all = rowCategoryRepository.findAll();
        assertThat(all)
                .as("수정 테스트를 위해 최소 1개 이상의 하위 카테고리 데이터가 필요합니다.")
                .isNotEmpty();

        RowCategory original = all.get(9);
        int rowCategoryId = original.getRowCategoryId();

        String updatedName = original.getName() + "_수정";

        // 🔹 2) 수정 요청 DTO 생성
        RowCategoryDTO requestDTO = RowCategoryDTO.builder()
                .rowCategoryId(rowCategoryId)
                .name(updatedName)
                .categoryId(original.getCategory().getCategoryId())
                .build();

        // 🔹 3) ModelMapper mock 설정 (RowCategory -> RowCategoryDTO)
        given(modelMapper.map(any(RowCategory.class), eq(RowCategoryDTO.class)))
                .willAnswer(invocation -> {
                    RowCategory source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    real.typeMap(RowCategory.class, RowCategoryDTO.class)
                            .addMappings(m -> {
                                m.map(RowCategory::getRowCategoryId, RowCategoryDTO::setRowCategoryId);
                                m.map(RowCategory::getName, RowCategoryDTO::setName);
                                m.map(rc -> rc.getCategory().getCategoryId(), RowCategoryDTO::setCategoryId);
                            });

                    return real.map(source, RowCategoryDTO.class);
                });

        // 🔹 4) 서비스 호출
        RowCategoryDTO result = rowCategoryService.modifyRowCategory(rowCategoryId, requestDTO);

        System.out.println("=== 🔥 modifyRowCategory 결과 DTO ===");
        System.out.println(result.getRowCategoryId() + " / " + result.getName());

        // 🔹 5) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getRowCategoryId()).isEqualTo(rowCategoryId);
        assertThat(result.getName()).isEqualTo(updatedName);

        // 🔹 6) 실제 DB 검증
        RowCategory updated = rowCategoryRepository.findById(rowCategoryId)
                .orElseThrow(() -> new AssertionError("수정된 하위 카테고리가 DB에 존재하지 않습니다."));

        assertThat(updated.getName()).isEqualTo(updatedName);
        assertThat(updated.getCategory().getCategoryId())
                .isEqualTo(original.getCategory().getCategoryId());
    }

    @Test
    @DisplayName("deleteRowCategory - 존재하는 하위 카테고리이면 정상적으로 삭제된다.")
    void deleteRowCategory_success() {
        // 🔹 1) 삭제 대상 하위 카테고리 하나 선택
        List<RowCategory> all = rowCategoryRepository.findAll();
        assertThat(all)
                .as("삭제 테스트를 위해 최소 1개 이상의 하위 카테고리 데이터가 필요합니다.")
                .isNotEmpty();

        RowCategory target = all.get(9);
        int rowCategoryId = target.getRowCategoryId();

        // 삭제 전에는 존재해야 함
        assertThat(rowCategoryRepository.existsById(rowCategoryId)).isTrue();

        // 🔹 2) 서비스 호출
        rowCategoryService.deleteRowCategory(rowCategoryId);

        // 🔹 3) 삭제 후에는 존재하지 않아야 함
        assertThat(rowCategoryRepository.existsById(rowCategoryId)).isFalse();
    }

    @Test
    @DisplayName("findAllProductsByRowCategoryId - 하위 카테고리의 전체 상품 목록을 조회한다.")
    void findAllProductsByRowCategoryId_success() {
        // 🔹 1) 하위 카테고리 하나 선택
        List<RowCategory> all = rowCategoryRepository.findAll();
        assertThat(all)
                .as("상품 조회 테스트를 위해 최소 1개 이상의 하위 카테고리 데이터가 필요합니다.")
                .isNotEmpty();

        RowCategory rowCategory = all.get(0);
        int rowCategoryId = rowCategory.getRowCategoryId();

        // 🔹 2) 해당 하위 카테고리에 속한 상품 실제 Entity 개수 확인
        List<Product> productEntities = productRepository.findByRowCategory(rowCategory);
        //  → ProductRepository 에 아래 메서드 있어야 함:
        // List<Product> findByRowCategory(RowCategory rowCategory);

        // 🔹 3) ModelMapper mock 설정 (Product -> ProductResponseDTO)
        given(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .willAnswer(invocation -> {
                    Product source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    // product.name -> productName 매핑 (ProductServiceTest와 동일 패턴)
                    real.typeMap(Product.class, ProductResponseDTO.class)
                            .addMappings(m -> m.map(Product::getName, ProductResponseDTO::setProductName));

                    return real.map(source, ProductResponseDTO.class);
                });

        // 🔹 4) 서비스 호출
        List<ProductResponseDTO> result = rowCategoryService.findAllProductsByRowCategoryId(rowCategoryId);

        System.out.println("=== 🔥 findAllProductsByRowCategoryId 결과 DTO 목록 ===");
        result.forEach(dto ->
                System.out.println(dto.getProductId() + " / " + dto.getProductName())
        );

        // 🔹 5) 검증
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(productEntities.size());
    }
}
