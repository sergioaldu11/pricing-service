package com.inditex.pricing.application.ports.out;

import com.inditex.pricing.domain.price.BrandId;
import com.inditex.pricing.domain.price.Price;
import com.inditex.pricing.domain.price.ProductId;
import java.time.LocalDateTime;
import java.util.Optional;

public interface PriceRepositoryPort {
    Optional<Price> findApplicable(
            BrandId brandId, ProductId productId, LocalDateTime applicationDate);
}
