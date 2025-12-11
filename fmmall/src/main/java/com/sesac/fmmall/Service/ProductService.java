package com.sesac.fmmall.Service;

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
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;
    private final RowCategoryRepository rowCategoryRepository;
    private final BrandRepository brandRepository;

    /* 상품 id로 상세 조회. */
    public ProductResponseDTO findProductByProductId(int productId) {
        Product foundProduct = productRepository.findById(productId).orElseThrow(
                () -> new IllegalArgumentException("해당 상품은 존재하지 않습니다."));

        return modelMapper.map(foundProduct, ProductResponseDTO.class);
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {

        Category category = categoryRepository.findById(productRequestDTO.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리입니다."));

        RowCategory rowCategory = rowCategoryRepository.findById(productRequestDTO.getRowCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 하위 카테고리 입니다."));

        Brand brand = brandRepository.findById(productRequestDTO.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));


        Product newProduct = Product.builder()
                .rowCategory(rowCategory)
                .category(category)
                .brand(brand)
                .name(productRequestDTO.getProductName())
                .price(productRequestDTO.getProductPrice())
                .description(productRequestDTO.getDescription())
                .isInstallationRequired(productRequestDTO.getIsInstallationRequired())
                .productStatus(productRequestDTO.getProductStatus())
                .build();

        Product savedProduct = productRepository.save(newProduct);

        return modelMapper.map(savedProduct, ProductResponseDTO.class);
    }

    @Transactional
    public ProductResponseDTO modifyProduct(int productId, ProductRequestDTO productRequestDTO) {

        Product foundProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("수정할 상품이 존재하지 않습니다."));

        Category newCategory = categoryRepository.findById(productRequestDTO.getCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상위 카테고리입니다."));

        RowCategory newRowCategory = rowCategoryRepository.findById(productRequestDTO.getRowCategoryCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 하위 카테고리 입니다."));

        Brand newBrand = brandRepository.findById(productRequestDTO.getBrandId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));

        foundProduct.modify(
                newCategory,
                newRowCategory,
                newBrand,
                productRequestDTO.getProductName(),
                productRequestDTO.getProductPrice(),
                productRequestDTO.getDescription(),
                productRequestDTO.getIsInstallationRequired(),
                productRequestDTO.getProductStatus()
        );
        return modelMapper.map(foundProduct, ProductResponseDTO.class);
    }

    @Transactional
    public void deleteProduct(int productId) {
        if(!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("삭제할 상품이 존재하지 않습니다.");
        }
        productRepository.deleteById(productId);
    }

    public List<ProductResponseDTO> findAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .collect(Collectors.toList());
    }
}
