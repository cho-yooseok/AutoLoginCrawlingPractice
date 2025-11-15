package com.example.yoogiscloset.koibito.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "koibito_products", schema = "ygkoibito")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KoibitoProduct {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String koibitoId;
    
    @Column(name = "product_name", nullable = false, length = 500)
    private String productName;
    
    @Column(name = "product_price", precision = 15, scale = 2)
    private BigDecimal productPrice;
    
    @Column(name = "brand", length = 100)
    private String brand;
    
    @Column(name = "brand_code", length = 100)
    private String brandCode;
    
    @Column(name = "koibito_store_price", precision = 15, scale = 2)
    private BigDecimal koibitoStorePrice;
    
    @Column(name = "detailed_size", length = 200)
    private String detailedSize;
    
    @Column(name = "product_grade", length = 50)
    private String productGrade;
    
    @Column(name = "accessories", columnDefinition = "TEXT")
    private String accessories;
    
    @Column(name = "koibito_inquiry_store", length = 200)
    private String koibitoInquiryStore;
    
    @Column(name = "koibito_code", length = 100)
    private String koibitoCode;
    
    @Column(name = "product_info", columnDefinition = "TEXT")
    private String productInfo;
    
    @Column(name = "image_urls", columnDefinition = "TEXT")
    private String imageUrls; // JSON 형태로 저장 (6장)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private ProductStatus status = ProductStatus.COLLECTED;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum ProductStatus {
        COLLECTED, PROCESSED, LISTED
    }
}