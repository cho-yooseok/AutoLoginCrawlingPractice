package com.example.yoogiscloset.koibito.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "고이비토 상품 정보 DTO (Data Transfer Object)")
public class KoibitoProductDto {
    
    @Schema(description = "고이비토 상품 고유 ID", example = "529756")
    private String koibitoId;
    
    @Schema(description = "상품명", example = "CHANEL 클래식 캐비어 미듐")
    private String productName;
    
    @Schema(description = "상품 가격", example = "12500000")
    private BigDecimal productPrice;
    
    @Schema(description = "브랜드명", example = "CHANEL")
    private String brand;
    
    @Schema(description = "브랜드 코드", example = "20000010000")
    private String brandCode;
    
    @Schema(description = "고이비토 매장 가격", example = "0")
    private BigDecimal koibitoStorePrice;
    
    @Schema(description = "상세 사이즈", example = "가로 25CM / 세로 15CM / 폭 6CM")
    private String detailedSize;
    
    @Schema(description = "상품 등급", example = "S")
    private String productGrade;
    
    @Schema(description = "부속품", example = "게런티카드 / 더스트 / 케이스")
    private String accessories;
    
    @Schema(description = "문의 매장", example = "강남본점")
    private String koibitoInquiryStore;
    
    @Schema(description = "고이비토 코드", example = "3004386-1")
    private String koibitoCode;
    
    @Schema(description = "상품 정보 요약", example = "전시상품급")
    private String productInfo;
    
    @Schema(description = "상품 이미지 URL 목록 (최대 6개)")
    private List<String> imageUrls;
}