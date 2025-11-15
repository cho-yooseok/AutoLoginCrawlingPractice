package com.example.yoogiscloset.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoogisclosetSellService {

    private final WebDriver webDriver;

    /**
     * 요기스트 판매 정보 입력 전체 프로세스 (여러 이미지 지원)
     * @param koibitoImageUrls 고이비토에서 추출한 모든 이미지 URL 목록
     */
    public String submitSellItemWithMultipleImages(List<String> koibitoImageUrls) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
            
            // 1. SELL TO US 클릭
            log.info("1단계: SELL TO US 클릭");
            WebElement sellToUsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.flex-row > div > div > div.topbar > div.fixed-width.row.d-md-flex.d-none.pl-2.pr-2.pt-2.pb-2.topbar-desktop > div.topbar-shops > div > div:nth-child(2) > div > a > span")
            ));
            new Actions(webDriver).moveToElement(sellToUsLink).pause(Duration.ofMillis(500)).click().perform();
            log.info("SELL TO US 클릭 완료");
            humanLikeDelay(2000, 3000);

            // 2. Submit New Item 버튼 클릭
            log.info("2단계: Submit New Item 버튼 클릭");
            WebElement submitNewItemButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.fixed-width.mt-4 > div.sell-index-container.row > div:nth-child(1) > div.row > div > div > div:nth-child(1) > button")
            ));
            new Actions(webDriver).moveToElement(submitNewItemButton).pause(Duration.ofMillis(500)).click().perform();
            log.info("Submit New Item 버튼 클릭 완료");
            humanLikeDelay(2000, 3000);

            // 3. Next 버튼 클릭
            log.info("3단계: Next 버튼 클릭");
            WebElement nextButton1 = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.pt-3 > div > button")
            ));
            new Actions(webDriver).moveToElement(nextButton1).pause(Duration.ofMillis(500)).click().perform();
            log.info("첫 번째 Next 버튼 클릭 완료");
            humanLikeDelay(2000, 3000);

            // 4. 사진 업로드 (모든 고이비토 이미지 업로드)
            log.info("4단계: 사진 업로드 - 모든 고이비토 이미지 업로드");
            
            // 고이비토 이미지 다운로드 (최대 5개 제한)
            List<String> downloadedImagePaths = new ArrayList<>();
            if (koibitoImageUrls != null && !koibitoImageUrls.isEmpty()) {
                int maxImages = Math.min(koibitoImageUrls.size(), 5); // 최대 5개 제한
                log.info("고이비토에서 " + koibitoImageUrls.size() + "개 이미지 발견, " + maxImages + "개만 업로드합니다.");
                
                for (int i = 0; i < maxImages; i++) {
                    String imageUrl = koibitoImageUrls.get(i);
                    String downloadedImagePath = downloadKoibitoImage(imageUrl);
                    if (downloadedImagePath != null) {
                        downloadedImagePaths.add(downloadedImagePath);
                        log.info("이미지 " + (i + 1) + " 다운로드 완료: " + imageUrl);
                    } else {
                        log.warn("이미지 " + (i + 1) + " 다운로드 실패: " + imageUrl);
                    }
                }
            }
            
            log.info("총 " + downloadedImagePaths.size() + "개의 이미지를 요기스트에 업로드합니다.");
            
            // 각 이미지에 대해 업로드 수행
            for (int i = 0; i < downloadedImagePaths.size(); i++) {
                String imagePath = downloadedImagePaths.get(i);
                log.info("이미지 " + (i + 1) + "/" + downloadedImagePaths.size() + " 업로드 시작: " + imagePath);
                
                try {
                    // 첫 번째 이미지는 사진 업로드 버튼, 나머지는 "Add Another Photo" 버튼 클릭
                    if (i == 0) {
                        // 첫 번째 이미지 - 사진 업로드 버튼 클릭
                        WebElement photoUploadButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.d-flex.flex-column.justify-content-start.p-3 > div:nth-child(2) > div:nth-child(1) > div > div > div:nth-child(3) > button")
                        ));
                        new Actions(webDriver).moveToElement(photoUploadButton).pause(Duration.ofMillis(500)).click().perform();
                        log.info("첫 번째 이미지 - 사진 업로드 버튼 클릭 완료");
                        humanLikeDelay(1000, 2000);
                    } else {
                        // 두 번째 이미지부터 - "Add Another Photo" 버튼 클릭
                        try {
                            WebElement addAnotherPhotoButton = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.d-flex.flex-column.justify-content-start.p-3 > div:nth-child(2) > div:nth-child(1) > div > div > div:nth-child(2) > div > button > span")
                            ));
                            new Actions(webDriver).moveToElement(addAnotherPhotoButton).pause(Duration.ofMillis(500)).click().perform();
                            log.info("이미지 " + (i + 1) + " - Add Another Photo 버튼 클릭 완료");
                            humanLikeDelay(1000, 2000);
                        } catch (Exception e) {
                            log.warn("Add Another Photo 버튼을 찾을 수 없습니다. 대체 방법을 시도합니다.");
                            // 대체 방법: 사진 업로드 버튼 클릭
                            WebElement photoUploadButton = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.d-flex.flex-column.justify-content-start.p-3 > div:nth-child(2) > div:nth-child(1) > div > div > div:nth-child(3) > button")
                            ));
                            new Actions(webDriver).moveToElement(photoUploadButton).pause(Duration.ofMillis(500)).click().perform();
                            log.info("이미지 " + (i + 1) + " - 대체 사진 업로드 버튼 클릭 완료");
                            humanLikeDelay(1000, 2000);
                        }
                    }

                    // 파일 선택 대화상자에서 이미지 파일 선택
                    try {
                        // 파일 입력 요소 찾기 (숨겨진 input[type='file'])
                        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[type='file']")
                        ));
                        
                        // 파일 경로 입력
                        fileInput.sendKeys(imagePath);
                        log.info("고이비토 이미지 파일 선택 완료: " + imagePath);
                        humanLikeDelay(2000, 3000);
                        
                        // 오픈 버튼 클릭 생략 - 파일이 자동으로 업로드됨
                        log.info("파일 경로 입력 완료. 자동 업로드 대기 중...");
                        humanLikeDelay(3000, 5000);
                        
                        // 파일 선택 대화상자 닫기 (ESC 키 또는 Enter 키)
                        try {
                            // ESC 키로 파일 선택 대화상자 닫기
                            new Actions(webDriver).sendKeys(Keys.ESCAPE).perform();
                            log.info("파일 선택 대화상자 닫기 완료 (ESC 키)");
                            humanLikeDelay(1000, 2000);
                        } catch (Exception e) {
                            log.warn("파일 선택 대화상자 닫기 실패: " + e.getMessage());
                        }
                        
                        log.info("이미지 " + (i + 1) + " 업로드 완료");
                        
                    } catch (Exception e) {
                        log.error("파일 업로드 중 오류 발생: " + e.getMessage());
                    }
                    
                } catch (Exception e) {
                    log.error("이미지 " + (i + 1) + " 업로드 실패: " + e.getMessage());
                }
            }
            
            // 모든 임시 파일 정리
            for (String imagePath : downloadedImagePaths) {
                cleanupTempFile(imagePath);
            }
            log.info("모든 이미지 업로드 및 임시 파일 정리 완료");
            
            // 모든 이미지 업로드 완료 후 파일 선택 대화상자 닫기
            try {
                // ESC 키로 파일 선택 대화상자 닫기
                new Actions(webDriver).sendKeys(Keys.ESCAPE).perform();
                log.info("모든 이미지 업로드 완료 후 파일 선택 대화상자 닫기 완료");
                humanLikeDelay(1000, 2000);
            } catch (Exception e) {
                log.warn("파일 선택 대화상자 닫기 실패: " + e.getMessage());
            }

            // 5. 디자이너 선택 (Chanel)
            log.info("5단계: 디자이너 선택 (Chanel)");
            WebElement designerSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("select[name='designer']")
            ));
            Select designerDropdown = new Select(designerSelect);
            designerDropdown.selectByValue("12"); // Chanel의 value는 "12"
            log.info("Chanel 디자이너 선택 완료");
            humanLikeDelay(1000, 2000);

            // 6. 스타일 입력 (handbag)
            log.info("6단계: 스타일 입력 (handbag)");
            WebElement styleInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[name='style']")
            ));
            new Actions(webDriver).moveToElement(styleInput).click().perform();
            styleInput.clear();
            humanLikeType(styleInput, "handbag");
            log.info("스타일 입력 완료: handbag");
            humanLikeDelay(1000, 2000);

            // 7. Next 버튼 클릭 (최종)
            log.info("7단계: 최종 Next 버튼 클릭");
            WebElement finalNextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.pt-3 > div > button.el-button.w-75.el-button--primary")
            ));
            new Actions(webDriver).moveToElement(finalNextButton).pause(Duration.ofMillis(500)).click().perform();
            log.info("최종 Next 버튼 클릭 완료");

            log.info("✅ 요기스트 판매 정보 입력 프로세스 완료");
            return "판매 정보 입력이 성공적으로 완료되었습니다.";

        } catch (Exception e) {
            log.error("요기스트 판매 정보 입력 중 오류 발생", e);
            throw new RuntimeException("판매 정보 입력에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 요기스트 판매 정보 입력 전체 프로세스 (단일 이미지)
     * @param koibitoImageUrl 고이비토에서 추출한 이미지 URL (첫 번째 이미지)
     */
    public String submitSellItem(String koibitoImageUrl) {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
            
            // 1. SELL TO US 클릭
            log.info("1단계: SELL TO US 클릭");
            WebElement sellToUsLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.flex-row > div > div > div.topbar > div.fixed-width.row.d-md-flex.d-none.pl-2.pr-2.pt-2.pb-2.topbar-desktop > div.topbar-shops > div > div:nth-child(2) > div > a > span")
            ));
            new Actions(webDriver).moveToElement(sellToUsLink).pause(Duration.ofMillis(500)).click().perform();
            log.info("SELL TO US 클릭 완료");
            humanLikeDelay(2000, 3000);

            // 2. Submit New Item 버튼 클릭
            log.info("2단계: Submit New Item 버튼 클릭");
            WebElement submitNewItemButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.fixed-width.mt-4 > div.sell-index-container.row > div:nth-child(1) > div.row > div > div > div:nth-child(1) > button")
            ));
            new Actions(webDriver).moveToElement(submitNewItemButton).pause(Duration.ofMillis(500)).click().perform();
            log.info("Submit New Item 버튼 클릭 완료");
            humanLikeDelay(2000, 3000);

            // 3. Next 버튼 클릭
            log.info("3단계: Next 버튼 클릭");
            WebElement nextButton1 = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.pt-3 > div > button")
            ));
            new Actions(webDriver).moveToElement(nextButton1).pause(Duration.ofMillis(500)).click().perform();
            log.info("첫 번째 Next 버튼 클릭 완료");
            humanLikeDelay(2000, 3000);

            // 4. 사진 업로드 (모든 고이비토 이미지 업로드)
            log.info("4단계: 사진 업로드 - 모든 고이비토 이미지 업로드");
            
            // 고이비토 이미지 URL들을 파싱하여 모든 이미지 다운로드
            List<String> downloadedImagePaths = new ArrayList<>();
            if (koibitoImageUrl != null && !koibitoImageUrl.isEmpty()) {
                // 단일 URL인 경우
                String downloadedImagePath = downloadKoibitoImage(koibitoImageUrl);
                if (downloadedImagePath != null) {
                    downloadedImagePaths.add(downloadedImagePath);
                }
            }
            
            log.info("총 " + downloadedImagePaths.size() + "개의 이미지를 업로드합니다.");
            
            // 각 이미지에 대해 업로드 수행
            for (int i = 0; i < downloadedImagePaths.size(); i++) {
                String imagePath = downloadedImagePaths.get(i);
                log.info("이미지 " + (i + 1) + "/" + downloadedImagePaths.size() + " 업로드 시작: " + imagePath);
                
                try {
                    // 첫 번째 이미지는 사진 업로드 버튼, 나머지는 "Add Another Photo" 버튼 클릭
                    if (i == 0) {
                        // 첫 번째 이미지 - 사진 업로드 버튼 클릭
                        WebElement photoUploadButton = wait.until(ExpectedConditions.elementToBeClickable(
                            By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.d-flex.flex-column.justify-content-start.p-3 > div:nth-child(2) > div:nth-child(1) > div > div > div:nth-child(3) > button")
                        ));
                        new Actions(webDriver).moveToElement(photoUploadButton).pause(Duration.ofMillis(500)).click().perform();
                        log.info("첫 번째 이미지 - 사진 업로드 버튼 클릭 완료");
                        humanLikeDelay(1000, 2000);
                    } else {
                        // 두 번째 이미지부터 - "Add Another Photo" 버튼 클릭
                        try {
                            WebElement addAnotherPhotoButton = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.d-flex.flex-column.justify-content-start.p-3 > div:nth-child(2) > div:nth-child(1) > div > div > div:nth-child(2) > div > button > span")
                            ));
                            new Actions(webDriver).moveToElement(addAnotherPhotoButton).pause(Duration.ofMillis(500)).click().perform();
                            log.info("이미지 " + (i + 1) + " - Add Another Photo 버튼 클릭 완료");
                            humanLikeDelay(1000, 2000);
                        } catch (Exception e) {
                            log.warn("Add Another Photo 버튼을 찾을 수 없습니다. 대체 방법을 시도합니다.");
                            // 대체 방법: 사진 업로드 버튼 클릭
                            WebElement photoUploadButton = wait.until(ExpectedConditions.elementToBeClickable(
                                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.d-flex.flex-column.justify-content-start.p-3 > div:nth-child(2) > div:nth-child(1) > div > div > div:nth-child(3) > button")
                            ));
                            new Actions(webDriver).moveToElement(photoUploadButton).pause(Duration.ofMillis(500)).click().perform();
                            log.info("이미지 " + (i + 1) + " - 대체 사진 업로드 버튼 클릭 완료");
                            humanLikeDelay(1000, 2000);
                        }
                    }

                    // 파일 선택 대화상자에서 이미지 파일 선택
                    try {
                        // 파일 입력 요소 찾기 (숨겨진 input[type='file'])
                        WebElement fileInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                            By.cssSelector("input[type='file']")
                        ));
                        
                        // 파일 경로 입력
                        fileInput.sendKeys(imagePath);
                        log.info("고이비토 이미지 파일 선택 완료: " + imagePath);
                        humanLikeDelay(2000, 3000);
                        
                        // 오픈 버튼 클릭 생략 - 파일이 자동으로 업로드됨
                        log.info("파일 경로 입력 완료. 자동 업로드 대기 중...");
                        humanLikeDelay(3000, 5000);
                        
                        // 파일 선택 대화상자 닫기 (ESC 키 또는 Enter 키)
                        try {
                            // ESC 키로 파일 선택 대화상자 닫기
                            new Actions(webDriver).sendKeys(Keys.ESCAPE).perform();
                            log.info("파일 선택 대화상자 닫기 완료 (ESC 키)");
                            humanLikeDelay(1000, 2000);
                        } catch (Exception e) {
                            log.warn("파일 선택 대화상자 닫기 실패: " + e.getMessage());
                        }
                        
                        log.info("이미지 " + (i + 1) + " 업로드 완료");
                        
                    } catch (Exception e) {
                        log.error("파일 업로드 중 오류 발생: " + e.getMessage());
                    }
                    
                } catch (Exception e) {
                    log.error("이미지 " + (i + 1) + " 업로드 실패: " + e.getMessage());
                }
            }
            
            // 모든 임시 파일 정리
            for (String imagePath : downloadedImagePaths) {
                cleanupTempFile(imagePath);
            }
            log.info("모든 이미지 업로드 및 임시 파일 정리 완료");
            
            // 모든 이미지 업로드 완료 후 파일 선택 대화상자 닫기
            try {
                // ESC 키로 파일 선택 대화상자 닫기
                new Actions(webDriver).sendKeys(Keys.ESCAPE).perform();
                log.info("모든 이미지 업로드 완료 후 파일 선택 대화상자 닫기 완료");
                humanLikeDelay(1000, 2000);
            } catch (Exception e) {
                log.warn("파일 선택 대화상자 닫기 실패: " + e.getMessage());
            }

            // 5. 디자이너 선택 (Chanel)
            log.info("5단계: 디자이너 선택 (Chanel)");
            WebElement designerSelect = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("select[name='designer']")
            ));
            Select designerDropdown = new Select(designerSelect);
            designerDropdown.selectByValue("12"); // Chanel의 value는 "12"
            log.info("Chanel 디자이너 선택 완료");
            humanLikeDelay(1000, 2000);

            // 6. 스타일 입력 (handbag)
            log.info("6단계: 스타일 입력 (handbag)");
            WebElement styleInput = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("input[name='style']")
            ));
            new Actions(webDriver).moveToElement(styleInput).click().perform();
            styleInput.clear();
            humanLikeType(styleInput, "handbag");
            log.info("스타일 입력 완료: handbag");
            humanLikeDelay(1000, 2000);

            // 7. Next 버튼 클릭 (최종)
            log.info("7단계: 최종 Next 버튼 클릭");
            WebElement finalNextButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#__layout > div > div.main.w-full > div.content-wrapper > div > div.container.fixed-width.mt-4 > div > div > div > div.pt-3 > div > button.el-button.w-75.el-button--primary")
            ));
            new Actions(webDriver).moveToElement(finalNextButton).pause(Duration.ofMillis(500)).click().perform();
            log.info("최종 Next 버튼 클릭 완료");

            log.info("✅ 요기스트 판매 정보 입력 프로세스 완료");
            return "판매 정보 입력이 성공적으로 완료되었습니다.";

        } catch (Exception e) {
            log.error("요기스트 판매 정보 입력 중 오류 발생", e);
            throw new RuntimeException("판매 정보 입력에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 고이비토 이미지 다운로드
     * @param imageUrl 이미지 URL
     * @return 다운로드된 파일 경로
     */
    private String downloadKoibitoImage(String imageUrl) {
        try {
            log.info("고이비토 이미지 다운로드 시작: " + imageUrl);

            // 임시 디렉토리 생성
            String tempDir = System.getProperty("java.io.tmpdir");
            String fileName = "koibito_image_" + System.currentTimeMillis() + ".jpg";
            String filePath = tempDir + File.separator + fileName;

            // 이미지 다운로드 (User-Agent 헤더 추가)
            URL url = new URL(imageUrl);
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            connection.setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");
            connection.setRequestProperty("Accept-Language", "ko-KR,ko;q=0.9,en;q=0.8");
            connection.setRequestProperty("Referer", "https://m.koibito.co.kr/");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);

            try (InputStream inputStream = connection.getInputStream();
                 FileOutputStream outputStream = new FileOutputStream(filePath)) {

                IOUtils.copy(inputStream, outputStream);
                log.info("고이비토 이미지 다운로드 완료: " + filePath);
                return filePath;
            }

        } catch (IOException e) {
            log.error("고이비토 이미지 다운로드 실패: " + imageUrl, e);
            return null;
        }
    }

    /**
     * 임시 파일 정리
     * @param filePath 파일 경로
     */
    private void cleanupTempFile(String filePath) {
        try {
            if (filePath != null) {
                File file = new File(filePath);
                if (file.exists() && file.delete()) {
                    log.info("임시 파일 삭제 완료: " + filePath);
                }
            }
        } catch (Exception e) {
            log.warn("임시 파일 삭제 실패: " + filePath, e);
        }
    }

    /**
     * 인간과 유사한 타이핑
     * @param element 입력 요소
     * @param text 입력할 텍스트
     */
    private void humanLikeType(WebElement element, String text) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            humanLikeDelay(50, 150);
        }
    }

    /**
     * 인간과 유사한 지연
     * @param min 최소 지연 시간 (ms)
     * @param max 최대 지연 시간 (ms)
     */
    private void humanLikeDelay(int min, int max) {
        try {
            int delay = new Random().nextInt(max - min + 1) + min;
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 현재 URL 반환
     * @return 현재 페이지 URL
     */
    public String getCurrentUrl() {
        return webDriver.getCurrentUrl();
    }

    /**
     * 현재 페이지 제목 반환
     * @return 현재 페이지 제목
     */
    public String getPageTitle() {
        return webDriver.getTitle();
    }
}