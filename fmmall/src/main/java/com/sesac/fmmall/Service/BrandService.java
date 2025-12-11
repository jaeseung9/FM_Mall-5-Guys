package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.BrandDTO;
import com.sesac.fmmall.DTO.Product.ProductResponseDTO;
import com.sesac.fmmall.Entity.Brand;
import com.sesac.fmmall.Entity.Product;
import com.sesac.fmmall.Repository.BrandRepository;
import com.sesac.fmmall.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    /* 브랜드 정보를 입력 후 브랜드 정보 등록/추가 진행. */
    @Transactional
    public BrandDTO insertBrand(BrandDTO brandDTO) {

        Brand newBrand = Brand.builder()
                .name(brandDTO.getName())
                .build();

        Brand savedBrand = brandRepository.save(newBrand);

        return modelMapper.map(savedBrand, BrandDTO.class);
    }

    /* brandId값을 넘겨, 해당 브랜드 정보 수정 진행. */
    @Transactional
    public BrandDTO modifyBrand(int brandId, BrandDTO brandDTO) {

        Brand foundBrand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));

        foundBrand.setName(brandDTO.getName());

        return modelMapper.map(foundBrand, BrandDTO.class);
    }

    /* brandId 값을 넘겨, 해당 브랜드 정보 삭제 진행. */
    @Transactional
    public void deleteBrand(int brandId) {

        if (!brandRepository.existsById(brandId)) {
            throw new IllegalArgumentException("삭제할 브랜드가 존재하지 않습니다.");
        }

        brandRepository.deleteById(brandId);
    }

    /* brandId 값을 넘겨, 해당 브랜드 상품 목록 조회 진행. */
    @Transactional
    public List<ProductResponseDTO> findAllProductsByBrandId(int brandId) {

        Brand foundBrand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 브랜드입니다."));

        List<Product> products = productRepository.findByBrand(foundBrand);

        return products.stream()
                .map(product -> modelMapper.map(product, ProductResponseDTO.class))
                .toList();
    }

    /* ✅ 브랜드 전체 목록 조회 (ADMIN용) */
    @Transactional
    public List<BrandDTO> findAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(brand -> BrandDTO.builder()
                        .brandId(brand.getBrandId())
                        .name(brand.getName())
                        .build())
                .toList();
    }
}
