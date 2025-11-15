package com.example.yoogiscloset.koibito.service;

import com.example.yoogiscloset.koibito.dto.KoibitoProductDto;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class KoibitoScrapingService {

    private final WebDriver webDriver;

    public KoibitoProductDto scrapeProduct(String koibitoUrl) {
        try {
            System.out.println("고이비토 상품 수집 시작: " + koibitoUrl);
            webDriver.get(koibitoUrl);
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
            
            // 페이지 로딩 대기
            Thread.sleep(3000);
            
            // 제품이름
            String productName = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_title > p.info_title")
            )).getText();
            
            // 제품가격
            String productPriceText = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_title > p.info_price.font_ptd")
            )).getText();
            BigDecimal productPrice = parsePrice(productPriceText);
            
            // 브랜드
            String brand = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(2)")
            )).getText();
            
            // 브랜드코드
            String brandCode = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(4)")
            )).getText();
            
            // 상세사이즈
            String detailedSize = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(7)")
            )).getText();
            
            // 제품등급
            String productGrade = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(9) > table > tbody > tr > td:nth-child(1)")
            )).getText();
            
            // 부속품
            String accessories = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(11)")
            )).getText();
            
            // 고이비토문의매장
            String koibitoInquiryStore = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(13) > table > tbody > tr > td")
            )).getText();
            
            // 고이비토코드
            String koibitoCode = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#wrap > div.detail_info_list > dl > dd:nth-child(15)")
            )).getText();
            
            // 상품정보
            String productInfo = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("#detail_contents > h4")
            )).getText();
            
            // 제품이미지 6장
            List<String> imageUrls = new ArrayList<>();
            List<WebElement> imageElements = webDriver.findElements(
                By.cssSelector("#wrap > div.flexslider > div > ul > li > img")
            );
            for (WebElement img : imageElements) {
                String src = img.getAttribute("src");
                if (src != null && !src.isEmpty()) {
                    imageUrls.add(src);
                }
            }
            
            // Koibito ID 추출 (URL에서)
            String koibitoId = extractKoibitoId(koibitoUrl);
            
            System.out.println("Koibito 상품 수집 완료: " + productName);
            
            return KoibitoProductDto.builder()
                .koibitoId(koibitoId)
                .productName(productName)
                .productPrice(productPrice)
                .brand(brand)
                .brandCode(brandCode)
                .detailedSize(detailedSize)
                .productGrade(productGrade)
                .accessories(accessories)
                .koibitoInquiryStore(koibitoInquiryStore)
                .koibitoCode(koibitoCode)
                .productInfo(productInfo)
                .imageUrls(imageUrls)
                .build();
                
        } catch (Exception e) {
            System.err.println("Koibito 상품 정보 수집 실패: " + e.getMessage());
            throw new RuntimeException("Koibito 상품 정보 수집 실패: " + e.getMessage(), e);
        }
    }

    private BigDecimal parsePrice(String priceText) {
        // 가격 문자열에서 숫자만 추출
        String cleanPrice = priceText.replaceAll("[^0-9]", "");
        if (cleanPrice.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(cleanPrice);
    }

    private String extractKoibitoId(String url) {
        // URL에서 no=529756 부분 추출
        Pattern pattern = Pattern.compile("no=(\\d+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("유효하지 않은 Koibito URL: " + url);
    }
}