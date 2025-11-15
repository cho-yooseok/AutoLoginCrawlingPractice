package com.example.yoogiscloset.controller;

import com.example.yoogiscloset.koibito.entity.KoibitoProduct;
import com.example.yoogiscloset.koibito.service.KoibitoDataService;
import com.example.yoogiscloset.service.YoogisclosetSellService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Tag(name = "Yoogiscloset Sell Integration API", description = "고이비토 상품 데이터와 연동하여 판매 등록을 자동화하는 통합 API")
@Slf4j
@RestController
@RequestMapping("/api/yoogiscloset/sell/integration")
@RequiredArgsConstructor
public class YoogisclosetSellIntegrationController {

    private final YoogisclosetSellService yoogisclosetSellService;
    private final KoibitoDataService koibitoDataService;
    private final ObjectMapper objectMapper;

    @PostMapping("/submit/{koibitoProductId}")
    @Operation(summary = "고이비토 상품 ID로 판매 정보 자동 입력", description = "DB에 저장된 고이비토 상품 ID를 사용하여, 해당 상품의 모든 이미지(최대 5개)를 유기스클로젯 판매 페이지에 자동으로 업로드하고 정보를 입력합니다.")
    public ResponseEntity<?> submitSellItemByKoibitoId(
            @Parameter(name = "koibitoProductId", description = "DB에 저장된 고이비토 상품의 ID", required = true, example = "1")
            @PathVariable("koibitoProductId") Long koibitoProductId) {
        try {
            log.info("고이비토 상품 ID로 요기스트 판매 정보 입력 시작: " + koibitoProductId);

            KoibitoProduct koibitoProduct = koibitoDataService.getProductById(koibitoProductId);
            log.info("고이비토 상품 조회 완료: " + koibitoProduct.getProductName());

            List<String> imageUrls = extractAllImageUrls(koibitoProduct.getImageUrls());
            log.info("추출된 이미지 URL 개수: " + imageUrls.size());
            for (int i = 0; i < imageUrls.size(); i++) {
                log.info("이미지 " + (i + 1) + ": " + imageUrls.get(i));
            }

            String result = yoogisclosetSellService.submitSellItemWithMultipleImages(imageUrls);

            log.info("고이비토 상품 기반 요기스트 판매 정보 입력 완료");
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", result,
                "koibitoProductId", koibitoProductId,
                "koibitoProductName", koibitoProduct.getProductName(),
                "uploadedImageCount", Math.min(imageUrls.size(), 5),
                "timestamp", LocalDateTime.now()
            ));

        } catch (Exception e) {
            log.error("고이비토 상품 기반 요기스트 판매 정보 입력 실패", e);
            return ResponseEntity.status(500).body(Map.of(
                "success", false,
                "error", "판매 정보 입력 실패",
                "message", e.getMessage(),
                "koibitoProductId", koibitoProductId,
                "timestamp", LocalDateTime.now()
            ));
        }
    }

    private List<String> extractAllImageUrls(String imageUrlsJson) {
        try {
            if (imageUrlsJson == null || imageUrlsJson.isEmpty() || "[]".equals(imageUrlsJson)) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(imageUrlsJson, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            log.error("이미지 URL JSON 파싱 실패: " + imageUrlsJson, e);
            return new ArrayList<>();
        }
    }
}