package com.inditex.pricing.application.ports.in;

import com.inditex.pricing.domain.price.BrandId;
import com.inditex.pricing.domain.price.ProductId;
import java.time.LocalDateTime;
import java.util.Objects;

public record GetApplicablePriceQuery(
        BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
    public GetApplicablePriceQuery {
        Objects.requireNonNull(brandId, "brandId");
        Objects.requireNonNull(productId, "productId");
        Objects.requireNonNull(applicationDate, "applicationDate");
    }

    public static GetApplicablePriceQuery of(
            long brandId, long productId, LocalDateTime applicationDate) {
        return new GetApplicablePriceQuery(
                BrandId.of(brandId), ProductId.of(productId), applicationDate);
    }
}
