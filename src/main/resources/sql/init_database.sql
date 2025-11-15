-- =====================================================
-- Yoogiscloset 프로젝트 데이터베이스 초기화 스크립트
-- =====================================================
-- 작성일: 2025-10-28
-- 목적: 고이비토 상품 스크래핑 및 유기스클로젯 판매 등록을 위한 데이터베이스 설정
-- =====================================================

-- 1. 데이터베이스 생성 (Docker Compose에서 자동 생성됨)
-- =====================================================
-- CREATE DATABASE IF NOT EXISTS ygkoibito 
-- CHARACTER SET utf8mb4 
-- COLLATE utf8mb4_unicode_ci;

-- 2. 데이터베이스 사용 (이미 연결된 데이터베이스 사용)
-- =====================================================
-- USE ygkoibito;

-- 3. 고이비토 상품 테이블 생성
-- =====================================================
CREATE TABLE IF NOT EXISTS koibito_products (
    -- 기본 키
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 고이비토 관련 정보
    koibito_id VARCHAR(255) NOT NULL UNIQUE COMMENT '고이비토 상품 고유 ID',
    koibito_code VARCHAR(100) COMMENT '고이비토 상품 코드',
    koibito_store_price DECIMAL(15,2) COMMENT '고이비토 매장 가격',
    koibito_inquiry_store VARCHAR(200) COMMENT '문의 매장',
    
    -- 상품 기본 정보
    product_name VARCHAR(500) NOT NULL COMMENT '상품명',
    product_price DECIMAL(15,2) COMMENT '상품 가격',
    product_grade VARCHAR(50) COMMENT '상품 등급 (S, A, B, C)',
    product_info TEXT COMMENT '상품 정보 요약',
    
    -- 브랜드 정보
    brand VARCHAR(100) COMMENT '브랜드명',
    brand_code VARCHAR(100) COMMENT '브랜드 코드',
    
    -- 상품 상세 정보
    detailed_size VARCHAR(200) COMMENT '상세 사이즈 정보',
    accessories TEXT COMMENT '부속품 정보',
    
    -- 이미지 정보
    image_urls TEXT COMMENT '상품 이미지 URL 목록 (JSON 형태)',
    
    -- 상태 관리
    status ENUM('COLLECTED', 'PROCESSED', 'LISTED') DEFAULT 'COLLECTED' COMMENT '상품 상태',
    
    -- 타임스탬프
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    -- 인덱스 설정
    INDEX idx_koibito_id (koibito_id) COMMENT '고이비토 ID 인덱스',
    INDEX idx_brand (brand) COMMENT '브랜드 인덱스',
    INDEX idx_status (status) COMMENT '상태 인덱스',
    INDEX idx_created_at (created_at) COMMENT '생성일시 인덱스',
    INDEX idx_product_price (product_price) COMMENT '가격 인덱스'
    
) ENGINE=InnoDB 
  DEFAULT CHARSET=utf8mb4 
  COLLATE=utf8mb4_unicode_ci
  COMMENT='고이비토 상품 정보 저장 테이블';

-- 4. 테이블 정보 확인
-- =====================================================
DESCRIBE koibito_products;

-- 5. 초기 데이터 확인
-- =====================================================
SELECT 
    COUNT(*) as total_products,
    COUNT(CASE WHEN status = 'COLLECTED' THEN 1 END) as collected_count,
    COUNT(CASE WHEN status = 'PROCESSED' THEN 1 END) as processed_count,
    COUNT(CASE WHEN status = 'LISTED' THEN 1 END) as listed_count
FROM koibito_products;

-- 6. 샘플 데이터 조회 (최근 5개)
-- =====================================================
SELECT 
    id,
    koibito_id,
    product_name,
    brand,
    product_price,
    status,
    created_at
FROM koibito_products 
ORDER BY created_at DESC 
LIMIT 5;

-- =====================================================
-- 스크립트 실행 완료
-- =====================================================
