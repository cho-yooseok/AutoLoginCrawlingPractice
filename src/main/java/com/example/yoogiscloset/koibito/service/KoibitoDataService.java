package com.example.yoogiscloset.koibito.service;

import com.example.yoogiscloset.koibito.dto.KoibitoProductDto;
import com.example.yoogiscloset.koibito.entity.KoibitoProduct;
import com.example.yoogiscloset.koibito.repository.KoibitoProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class KoibitoDataService {

    private final KoibitoProductRepository koibitoProductRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 상품 정보를 저장하거나 이미 존재하면 업데이트합니다. (Upsert)
     * @param dto 스크래핑으로 수집한 상품 DTO
     * @return 저장되거나 업데이트된 상품 Entity
     */
    public KoibitoProduct saveProduct(KoibitoProductDto dto) {
        // koibitoId를 기준으로 이미 저장된 상품인지 확인합니다.
        Optional<KoibitoProduct> existingProductOptional = koibitoProductRepository.findByKoibitoId(dto.getKoibitoId());
        
        KoibitoProduct product;
        if (existingProductOptional.isPresent()) {
            // 1. 상품이 이미 존재할 경우: 기존 정보를 업데이트합니다.
            product = existingProductOptional.get();
            System.out.println("기존 상품 발견. 정보를 업데이트합니다: " + product.getKoibitoId());
            
            // DTO의 최신 정보로 Entity 필드를 업데이트
            product.setProductName(dto.getProductName());
            product.setProductPrice(dto.getProductPrice());
            product.setBrand(dto.getBrand());
            product.setBrandCode(dto.getBrandCode());
            product.setKoibitoStorePrice(dto.getKoibitoStorePrice());
            product.setDetailedSize(dto.getDetailedSize());
            product.setProductGrade(dto.getProductGrade());
            product.setAccessories(dto.getAccessories());
            product.setKoibitoInquiryStore(dto.getKoibitoInquiryStore());
            product.setKoibitoCode(dto.getKoibitoCode());
            product.setProductInfo(dto.getProductInfo());
            product.setImageUrls(convertImageUrlsToJson(dto.getImageUrls()));
            // 상태(status)는 비즈니스 로직에 따라 변경할 수 있습니다.
            
        } else {
            // 2. 상품이 존재하지 않을 경우: 새로 생성합니다.
             System.out.println("새로운 상품입니다. 데이터베이스에 저장합니다: " + dto.getKoibitoId());
            product = KoibitoProduct.builder()
                .koibitoId(dto.getKoibitoId())
                .productName(dto.getProductName())
                .productPrice(dto.getProductPrice())
                .brand(dto.getBrand())
                .brandCode(dto.getBrandCode())
                .koibitoStorePrice(dto.getKoibitoStorePrice())
                .detailedSize(dto.getDetailedSize())
                .productGrade(dto.getProductGrade())
                .accessories(dto.getAccessories())
                .koibitoInquiryStore(dto.getKoibitoInquiryStore())
                .koibitoCode(dto.getKoibitoCode())
                .productInfo(dto.getProductInfo())
                .imageUrls(convertImageUrlsToJson(dto.getImageUrls()))
                .status(KoibitoProduct.ProductStatus.COLLECTED)
                .build();
        }

        return koibitoProductRepository.save(product);
    }

    public List<KoibitoProduct> getAllProducts() {
        return koibitoProductRepository.findAll();
    }

    public KoibitoProduct getProductById(Long id) {
        return koibitoProductRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("상품을 찾을 수 없습니다: " + id));
    }

    private String convertImageUrlsToJson(List<String> imageUrls) {
        try {
            return objectMapper.writeValueAsString(imageUrls);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("이미지 URL JSON 변환 실패", e);
        }
    }
}