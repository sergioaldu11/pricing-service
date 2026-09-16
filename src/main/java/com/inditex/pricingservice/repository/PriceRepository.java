package com.inditex.pricingservice.repository;

import com.inditex.pricingservice.model.Price;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PriceRepository extends JpaRepository<Price, Long> {

    @Query("SELECT p FROM Price p " +
            "WHERE p.brandId = :brandId " +
            "AND p.productId = :productId " +
            "AND p.startDate <= :applyDate " +
            "AND p.endDate >= :applyDate " +
            "AND p.priority = (SELECT MAX(p2.priority) FROM Price p2 " +
            "WHERE p2.brandId = :brandId " +
            "AND p2.productId = :productId " +
            "AND p2.startDate <= :applyDate " +
            "AND p2.endDate >= :applyDate)")
    Optional<Price> findTopByBrandIdAndProductIdAndDate(@Param("brandId") Long brandId,
                                                        @Param("productId") Long productId,
                                                        @Param("applyDate") LocalDateTime applyDate);
}
