package com.example.yoogiscloset.koibito.controller;

import com.example.yoogiscloset.koibito.dto.KoibitoProductDto;
import com.example.yoogiscloset.koibito.entity.KoibitoProduct;
import com.example.yoogiscloset.koibito.service.KoibitoDataService;
import com.example.yoogiscloset.koibito.service.KoibitoScrapingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "Koibito API", description = "고이비토 상품 스크래핑 및 데이터 관리 API")
@RestController
@RequestMapping("/api/yg/koibito")
@RequiredArgsConstructor
public class KoibitoController {
    
    private final KoibitoScrapingService koibitoScrapingService;
    private final KoibitoDataService koibitoDataService;
    
    @PostMapping("/scrape")
    @Operation(summary = "고이비토 상품 정보 스크래핑 및 저장", description = "제공된 URL의 고이비토 상품 정보를 스크래핑하여 DB에 저장합니다.")
    public ResponseEntity<?> scrapeProduct(
            @Parameter(description = "스크래핑할 고이비토 상품 URL", required = true, example = "https://m.koibito.co.kr/goods_view.html?no=581471") 
            @RequestParam("url") String url) {

        try {
            System.out.println("고이비토 상품 수집 시작: " + url);
            
            KoibitoProductDto productDto = koibitoScrapingService.scrapeProduct(url);
            KoibitoProduct savedProduct = koibitoDataService.saveProduct(productDto);
            
            System.out.println("고이비토 상품 수집 완료: " + savedProduct.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "상품 정보가 성공적으로 수집되었습니다",
                "productId", savedProduct.getId(),
                "productName", savedProduct.getProductName(),
                "brand", savedProduct.getBrand(),
                "price", savedProduct.getProductPrice()
            ));
            
        } catch (Exception e) {
            System.err.println("고이비토 상품 수집 실패: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "상품 수집 실패",
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
            ));
        }
    }
    
    @GetMapping("/products")
    @Operation(summary = "모든 고이비토 상품 조회", description = "DB에 저장된 모든 고이비토 상품 목록을 조회합니다.")
    public ResponseEntity<List<KoibitoProduct>> getAllProducts() {
        try {
            List<KoibitoProduct> products = koibitoDataService.getAllProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
    
    @GetMapping("/products/{id}")
    @Operation(summary = "ID로 특정 상품 조회", description = "상품 ID를 사용하여 특정 고이비토 상품 정보를 조회합니다.")
    public ResponseEntity<KoibitoProduct> getProductById(
            @Parameter(name = "id", description = "조회할 상품의 ID", required = true, example = "1") 
            @PathVariable("id") Long id) {
        try {
            KoibitoProduct product = koibitoDataService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.status(404).body(null);
        }
    }
    

}



/*
 * 고이비토

https://m.koibito.co.kr/goods_view.html?no=581471

https://m.koibito.co.kr/goods_view.html?no=584256
 */