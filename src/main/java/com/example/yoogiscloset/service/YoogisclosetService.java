package com.example.yoogiscloset.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class YoogisclosetService {

    private final WebDriver webDriver;

    @Value("${yoogiscloset.username}")
    private String username;

    @Value("${yoogiscloset.password}")
    private String password;

    /**
     * 로그인 과정을 수행하고, 성공 여부를 확인하지 않은 채 바로 쿠키를 반환하는 메소드
     */
    public Set<Cookie> loginAndGetCookies() {
        try {
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(15));
            webDriver.get("https://www.yoogiscloset.com/");
            log.info("유기스클로젯 홈페이지 접속 성공");
            
            try {
                humanLikeDelay(500, 1200);
                WebElement xMarkIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#x-mark-icon")));
                new Actions(webDriver).moveToElement(xMarkIcon, 5, 5).pause(Duration.ofMillis(300)).click().perform();
                log.info("팝업창 엑스 버튼 클릭 완료");
            } catch (Exception e) {
                log.warn("팝업창이 없거나 이미 사라진 상태입니다.");
            }
            WebElement signInLink = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#__layout > div > div.main.w-full > div.flex-row > div > div > div.header-container > div.fixed-width.desktop > div > div.d-flex.mr-md-2.utility-nav > div.menu-item.mr-3 > div")));
            new Actions(webDriver).moveToElement(signInLink).pause(Duration.ofMillis(400)).click().perform();
            log.info("'Sign In' 링크 클릭 완료");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.el-dialog__body")));
            log.info("로그인 팝업창 확인 완료");
            WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#username")));
            new Actions(webDriver).moveToElement(emailInput).click().perform();
            emailInput.clear();
            humanLikeType(emailInput, username);
            WebElement passwordInput = webDriver.findElement(By.cssSelector("#password"));
            new Actions(webDriver).moveToElement(passwordInput).click().perform();
            passwordInput.clear();
            humanLikeType(passwordInput, password);
            humanLikeDelay(800, 2200);
            WebElement signInButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("#__layout > div > div.main.w-full > div:nth-child(4) > div > div > div.el-dialog__body > div > div > div.row.mb-4 > div > div.mb-3.mt-2.d-flex.justify-content-center > button")));
            new Actions(webDriver).moveToElement(signInButton).pause(Duration.ofMillis(500)).click().perform();
            log.info("'SIGN IN' 버튼 클릭, 로그인 시도");

            log.info("로그인 후 쿠키 생성을 위해 3~5초 대기합니다...");
            humanLikeDelay(3000, 5000);

            log.info("쿠키 추출을 시도합니다.");
            Set<Cookie> cookies = webDriver.manage().getCookies();
            log.info("쿠키 추출을 완료하였습니다。");
            return cookies;

        } catch (Exception e) {
            log.error("로그인 및 쿠키 추출 과정에서 오류 발생", e);
            throw new RuntimeException("자동 로그인 및 쿠키 추출에 실패했습니다.");
        }
    }
    
    private void humanLikeType(WebElement element, String text) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            humanLikeDelay(100, 350);
        }
    }

    private void humanLikeDelay(int minMillis, int maxMillis) {
        try {
            Thread.sleep(new Random().nextInt(maxMillis - minMillis + 1) + minMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}