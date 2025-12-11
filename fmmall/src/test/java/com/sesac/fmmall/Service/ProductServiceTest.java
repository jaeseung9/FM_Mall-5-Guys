package com.sesac.fmmall.Service;

import com.sesac.fmmall.Constant.ProductStatus;
import com.sesac.fmmall.DTO.Product.ProductRequestDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Entity.Brand;
import com.sesac.fmmall.Entity.Category;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Entity.RowCategory;
import com.sesac.fmmall.Repository.BrandRepository;
import com.sesac.fmmall.Repository.CategoryRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import com.sesac.fmmall.Repository.RowCategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RowCategoryRepository rowCategoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    // SecurityConfig 때문에 필요한 MockBean 들
    @MockBean
    private PasswordEncoder passwordEncoder;

    // ⚠ 핵심: ModelMapper도 MockBean으로 등록 (OrderService, ProductService 둘 다 이걸 주입받음)
    @MockBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("ProductId값으로 상품을 조회하면 DTO에 상품명이 들어온다.")
    void findProductByProductId() {
        // 1) DB에서 아무 상품이나 하나 꺼냄
        List<Product> allProducts = productRepository.findAll();
        assertThat(allProducts).isNotEmpty();

        Product foundProduct = allProducts.get(0);
        int productId = foundProduct.getProductId();

        System.out.println("=== 🔥 DB에서 읽어온 Entity ===");
        System.out.println(foundProduct.getProductId() + " / " + foundProduct.getName());

        // 2) ModelMapper mock 이 진짜 매핑을 하도록 설정 + name -> productName 매핑
        given(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .willAnswer(invocation -> {
                    Product source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    // 🧩 엔티티의 name -> DTO의 productName 으로 매핑
                    real.typeMap(Product.class, ProductResponseDTO.class)
                            .addMappings(m -> m.map(Product::getName, ProductResponseDTO::setProductName));

                    return real.map(source, ProductResponseDTO.class);
                });

        // 3) 서비스 호출
        ProductResponseDTO productDTO = productService.findProductByProductId(productId);

        System.out.println("=== 🔥 DTO로 변환된 결과 ===");
        System.out.println(productDTO.getProductId() + " / " + productDTO.getProductName());

        // 4) 검증
        assertThat(productDTO).isNotNull();
        assertThat(productDTO.getProductId()).isEqualTo(productId);
        assertThat(productDTO.getProductName()).isEqualTo(foundProduct.getName());
    }

    @Test
    @DisplayName("createProduct - 정상 요청 시 상품이 생성되고 DTO가 반환된다.")
    void createProduct_success() {
        // 🔹 1) 테스트에 사용할 카테고리 / 하위 카테고리 하나씩 확보
        List<Category> categories = categoryRepository.findAll();
        assertThat(categories)
                .as("테스트용 상위 카테고리 데이터가 DB에 있어야 합니다.")
                .isNotEmpty();

        List<RowCategory> rowCategories = rowCategoryRepository.findAll();
        assertThat(rowCategories)
                .as("테스트용 하위 카테고리 데이터가 DB에 있어야 합니다.")
                .isNotEmpty();

        Category category = categories.get(0);
        RowCategory rowCategory = rowCategories.get(0);

        // 🔹 2) 요청 DTO 생성
        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .productName("통합테스트 상품")
                .productPrice(150000)
                .stockQuantity(20)
                .capacity("10kg")
                .sizeInch(BigDecimal.valueOf(55.0))
                .description("통합테스트용 상품입니다.")
                .productStatus(ProductStatus.ACTIVE)
                .modelName("FM-TEST-001")
                .isInstallationRequired("Y")
                .brandId(1) // 브랜드는 현재 서비스에서 안 쓰더라도 일단 값 세팅
                .categoryId(category.getCategoryId())
                .rowCategoryId(rowCategory.getRowCategoryId())
                .build();

        // 🔹 3) ModelMapper mock -> 실제 매핑 수행하게 설정
        given(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .willAnswer(invocation -> {
                    Product source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    real.typeMap(Product.class, ProductResponseDTO.class)
                            .addMappings(m -> m.map(Product::getName, ProductResponseDTO::setProductName));

                    return real.map(source, ProductResponseDTO.class);
                });

        // 🔹 4) 서비스 호출
        ProductResponseDTO result = productService.createProduct(requestDTO);

        // 🔹 5) 검증
        assertThat(result).isNotNull();
        assertThat(result.getProductName()).isEqualTo(requestDTO.getProductName());
        assertThat(result.getProductPrice()).isEqualTo(requestDTO.getProductPrice());

        // 실제 DB에 잘 들어갔는지도 한 번 확인 (productId 기준으로 조회)
        Product saved = productRepository.findById(result.getProductId())
                .orElseThrow(() -> new AssertionError("생성된 상품이 DB에 존재하지 않습니다."));

        assertThat(saved.getName()).isEqualTo(requestDTO.getProductName());
        assertThat(saved.getPrice()).isEqualTo(requestDTO.getProductPrice());
        assertThat(saved.getCategory().getCategoryId()).isEqualTo(category.getCategoryId());
        assertThat(saved.getRowCategory().getRowCategoryId()).isEqualTo(rowCategory.getRowCategoryId());
    }

    @Test
    @DisplayName("modifyProduct - 기존 상품을 수정하면 변경 내용이 반영되어 DTO로 반환된다.")
    void modifyProduct_success() {
        // 🔹 1) DB에서 수정 대상 상품 하나 선택
        List<Product> allProducts = productRepository.findAll();
        assertThat(allProducts)
                .as("수정 테스트를 위해 최소 1개 이상의 상품이 DB에 있어야 합니다.")
                .isNotEmpty();

        Product original = allProducts.get(450);
        int productId = original.getProductId();

        Category originalCategory = original.getCategory();
        RowCategory originalRowCategory = original.getRowCategory();
        Brand originalBrand = original.getBrand();

        // 🔹 2) 수정할 값 세팅 (이름/가격/설명 등 변경)
        String updatedName = original.getName() + "_수정";
        int updatedPrice = (original.getPrice() != null ? original.getPrice() : 0) + 1000;
        String updatedDescription = "수정된 설명입니다.";
        String updatedIsInstallationRequired = "N"; // 예: 기존과 다르게
        ProductStatus updatedStatus = original.getProductStatus(); // 상태는 그대로 두거나 필요 시 변경

        ProductRequestDTO requestDTO = ProductRequestDTO.builder()
                .productName(updatedName)
                .productPrice(updatedPrice)
                .stockQuantity(original.getStockQuantity() != null ? original.getStockQuantity() : 0)
                .capacity(original.getCapacity())
                .sizeInch(original.getSizeInch() != null ? original.getSizeInch() : BigDecimal.ZERO)
                .description(updatedDescription)
                .productStatus(updatedStatus)
                .modelName(original.getModelName())
                .isInstallationRequired(updatedIsInstallationRequired)
                .brandId(originalBrand.getBrandId())
                .categoryId(originalCategory.getCategoryId())
                .rowCategoryId(originalRowCategory.getRowCategoryId())
                .build();

        // 🔹 3) ModelMapper mock → 실제 매핑 수행하도록 설정 (기존 테스트와 동일 패턴)
        given(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .willAnswer(invocation -> {
                    Product source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                            .setFieldMatchingEnabled(true);

                    real.typeMap(Product.class, ProductResponseDTO.class)
                            .addMappings(m -> m.map(Product::getName, ProductResponseDTO::setProductName));

                    return real.map(source, ProductResponseDTO.class);
                });

        // 🔹 4) 서비스 호출
        ProductResponseDTO result = productService.modifyProduct(productId, requestDTO);

        // 🔹 5) DTO 검증
        assertThat(result).isNotNull();
        assertThat(result.getProductId()).isEqualTo(productId);
        assertThat(result.getProductName()).isEqualTo(updatedName);
        assertThat(result.getProductPrice()).isEqualTo(updatedPrice);
        assertThat(result.getDescription()).isEqualTo(updatedDescription);

        // 🔹 6) 실제 DB에 반영됐는지 검증 (영속성 컨텍스트/더티체킹 결과 확인)
        Product updatedEntity = productRepository.findById(productId)
                .orElseThrow(() -> new AssertionError("수정된 상품이 DB에 존재하지 않습니다."));

        assertThat(updatedEntity.getName()).isEqualTo(updatedName);
        assertThat(updatedEntity.getPrice()).isEqualTo(updatedPrice);
        assertThat(updatedEntity.getDescription()).isEqualTo(updatedDescription);
        assertThat(updatedEntity.getIsInstallationRequired()).isEqualTo(updatedIsInstallationRequired);
        assertThat(updatedEntity.getCategory().getCategoryId()).isEqualTo(originalCategory.getCategoryId());
        assertThat(updatedEntity.getRowCategory().getRowCategoryId()).isEqualTo(originalRowCategory.getRowCategoryId());
        assertThat(updatedEntity.getBrand().getBrandId()).isEqualTo(originalBrand.getBrandId());
    }

    @Test
    @DisplayName("deleteProduct - 존재하는 상품이면 정상적으로 삭제된다.")
    void deleteProduct_success() {
        // 🔹 1) DB에서 아무 상품이나 하나 가져오기
        List<Product> allProducts = productRepository.findAll();
        assertThat(allProducts)
                .as("삭제 테스트를 위해 최소 1개 이상의 상품이 DB에 있어야 합니다.")
                .isNotEmpty();

        Product target = allProducts.get(450);
        int productId = target.getProductId();

        // 삭제 전에는 존재해야 함
        assertThat(productRepository.existsById(productId)).isTrue();

        // 🔹 2) 서비스 호출
        productService.deleteProduct(productId);

        // 🔹 3) 삭제 후에는 존재하지 않아야 함
        assertThat(productRepository.existsById(productId)).isFalse();
    }
}
