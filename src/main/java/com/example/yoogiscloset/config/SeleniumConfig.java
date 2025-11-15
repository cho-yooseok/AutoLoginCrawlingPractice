package com.example.yoogiscloset.config;

import jakarta.annotation.PostConstruct;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;

@Configuration
public class SeleniumConfig {

    @Value("${selenium.hub.url:http://localhost:4444/wd/hub}")
    private String seleniumHubUrl;

    @Value("${selenium.use.remote:false}")
    private boolean useRemoteDriver;

    @PostConstruct
    void postConstruct() {
        // Docker 환경이 아닌 경우에만 로컬 chromedriver 설정
        if (!useRemoteDriver) {
            String chromeDriverPath = "C:\\Users\\code\\Downloads\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe";
            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
        }
    }

    @Bean
    public WebDriver webDriver() {
        ChromeOptions options = createChromeOptions();

        // Docker 환경에서는 RemoteWebDriver 사용
        if (useRemoteDriver) {
            try {
                return new RemoteWebDriver(new URL(seleniumHubUrl), options);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Selenium Hub URL이 올바르지 않습니다: " + seleniumHubUrl, e);
            }
        }

        // 로컬 환경에서는 ChromeDriver 사용
        return new ChromeDriver(options);
    }

    private ChromeOptions createChromeOptions() {
        ChromeOptions options = new ChromeOptions();

        // --- 봇 탐지 우회 옵션 ---
        // Docker 환경에서는 headless 모드 권장
        if (useRemoteDriver) {
            options.addArguments("--headless");
        }

        // 2. 자동화 제어 표시줄 및 관련 속성 비활성화
        options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("--disable-blink-features=AutomationControlled");

        // 3. PC 환경과 동일한 창 크기 설정
        options.addArguments("--window-size=1920,1080");

        // 4. 일반적인 사용자 정보로 위장
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36");
        
        // --- Docker 환경을 위한 안정성 옵션 ---
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-plugins");
        options.addArguments("--disable-images");
        options.addArguments("--disable-javascript");

        return options;
    }
}