package com.example.yoogiscloset.koibito.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.yoogiscloset.koibito.entity.KoibitoProduct;

import java.util.Optional;

@Repository
public interface KoibitoProductRepository extends JpaRepository<KoibitoProduct, Long> {
    
    Optional<KoibitoProduct> findByKoibitoId(String koibitoId);
       
    @Modifying
    @Query(value = "ALTER TABLE koibito_products AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
