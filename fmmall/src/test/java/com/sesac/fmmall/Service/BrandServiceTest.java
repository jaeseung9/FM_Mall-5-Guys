package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.BrandDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Entity.Brand;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Repository.BrandRepository;
import com.sesac.fmmall.Repository.ProductRepository;
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
class BrandServiceTest {

    @Autowired
    private BrandService brandService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ProductRepository productRepository;

    @MockBean
    private ModelMapper modelMapper;

    @Test
    @DisplayName("insertBrand - 브랜드 등록 시 DB 저장 + DTO 반환")
    void insertBrand_success() {

        BrandDTO request = BrandDTO.builder()
                .name("테스트브랜드")
                .build();

        // ModelMapper mock → 실제 매핑 수행하도록 설정
        given(modelMapper.map(any(Brand.class), eq(BrandDTO.class)))
                .willAnswer(invocation -> {
                    Brand source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldMatchingEnabled(true)
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

                    real.typeMap(Brand.class, BrandDTO.class)
                            .addMappings(m -> {
                                m.map(Brand::getBrandId, BrandDTO::setBrandId);
                                m.map(Brand::getName, BrandDTO::setName);
                            });

                    return real.map(source, BrandDTO.class);
                });

        BrandDTO result = brandService.insertBrand(request);

        assertThat(result).isNotNull();
        assertThat(result.getBrandId()).isGreaterThan(0);
        assertThat(result.getName()).isEqualTo("테스트브랜드");

        Brand saved = brandRepository.findById(result.getBrandId())
                .orElseThrow();

        assertThat(saved.getName()).isEqualTo("테스트브랜드");
    }

    @Test
    @DisplayName("modifyBrand - 브랜드 정보 수정 후 DTO 반환")
    void modifyBrand_success() {

        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).isNotEmpty();

        Brand original = brands.get(0);
        int brandId = original.getBrandId();

        String updatedName = original.getName() + "_수정";

        BrandDTO request = BrandDTO.builder()
                .brandId(brandId)
                .name(updatedName)
                .build();

        // Mapper mock 설정
        given(modelMapper.map(any(Brand.class), eq(BrandDTO.class)))
                .willAnswer(invocation -> {
                    Brand source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldMatchingEnabled(true)
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

                    real.typeMap(Brand.class, BrandDTO.class)
                            .addMappings(m -> {
                                m.map(Brand::getBrandId, BrandDTO::setBrandId);
                                m.map(Brand::getName, BrandDTO::setName);
                            });

                    return real.map(source, BrandDTO.class);
                });

        BrandDTO result = brandService.modifyBrand(brandId, request);

        assertThat(result.getBrandId()).isEqualTo(brandId);
        assertThat(result.getName()).isEqualTo(updatedName);

        Brand updated = brandRepository.findById(brandId)
                .orElseThrow();

        assertThat(updated.getName()).isEqualTo(updatedName);
    }

    @Test
    @DisplayName("deleteBrand - 브랜드 삭제 성공")
    void deleteBrand_success() {

        List<Brand> brands = brandRepository.findAll();
        assertThat(brands).isNotEmpty();

        Brand target = brands.get(3);
        int brandId = target.getBrandId();

        assertThat(brandRepository.existsById(brandId)).isTrue();

        brandService.deleteBrand(brandId);

        assertThat(brandRepository.existsById(brandId)).isFalse();
    }

    @Test
    @DisplayName("findAllProductsByBrandId - 브랜드 상품 목록 조회 + 실제 목록 출력")
    void findAllProductsByBrandId_success() {
        // 🔹 1) 브랜드 하나 선택
        List<Brand> brands = brandRepository.findAll();
        assertThat(brands)
                .as("브랜드별 상품 조회를 위해 최소 1개 이상의 브랜드가 필요합니다.")
                .isNotEmpty();

        Brand brand = brands.get(0);

        // 🔹 2) 이 브랜드에 실제로 묶여 있는 상품 엔티티들 조회 (검증용)
        List<Product> productEntities = productRepository.findByBrand(brand);

        // 만약 여기서 빈 리스트면, 당연히 서비스 호출 결과도 비어 있음.
        System.out.println("=== 🔥 DB 기준 브랜드 [" + brand.getName() + "] 에 연결된 상품 수: "
                + productEntities.size() + "개 ===");

        // 🔹 3) ModelMapper mock 설정 (Product -> ProductResponseDTO)
        given(modelMapper.map(any(Product.class), eq(ProductResponseDTO.class)))
                .willAnswer(invocation -> {
                    Product source = invocation.getArgument(0);

                    ModelMapper real = new ModelMapper();
                    real.getConfiguration()
                            .setFieldMatchingEnabled(true)
                            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

                    // Entity의 name -> DTO의 productName으로 매핑
                    real.typeMap(Product.class, ProductResponseDTO.class)
                            .addMappings(m -> m.map(Product::getName, ProductResponseDTO::setProductName));

                    return real.map(source, ProductResponseDTO.class);
                });

        // 🔹 4) 서비스 호출
        List<ProductResponseDTO> result = brandService.findAllProductsByBrandId(brand.getBrandId());

        // 🔹 5) 실제로 브랜드별 상품이 어떻게 나오는지 콘솔에 출력
        System.out.println("=== ✅ 브랜드 [" + brand.getName() + "] 상품 목록 (DTO) ===");
        result.forEach(dto -> System.out.println(
                "productId=" + dto.getProductId()
                        + " / productName=" + dto.getProductName()
                        + " / price=" + dto.getProductPrice()
        ));
        System.out.println("=== 총 " + result.size() + "개 ===");

        // 🔹 6) 검증
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(productEntities.size());
    }
}
