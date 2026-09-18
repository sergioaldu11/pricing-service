package com.inditex.pricing.domain.price;

import com.inditex.pricing.domain.shared.Validator;
import java.time.LocalDateTime;

public record Price(
        BrandId brandId,
        ProductId productId,
        PriceListId priceListId,
        Priority priority,
        Money finalPrice,
        LocalDateTime startDate,
        LocalDateTime endDate) {

    public Price {
        Validator.create()
                .requireNonNull(brandId, "brandId")
                .requireNonNull(productId, "productId")
                .requireNonNull(priceListId, "priceListId")
                .requireNonNull(priority, "priority")
                .requireNonNull(finalPrice, "finalPrice")
                .requireNonNull(startDate, "startDate")
                .requireNonNull(endDate, "endDate")
                .throwIfInvalid();

        Validator.create()
                .requireTrue(
                        !endDate.isBefore(startDate), "dateRange", "endDate must be >= startDate")
                .throwIfInvalid();
    }

    public static Price create(
            BrandId brandId,
            ProductId productId,
            PriceListId priceListId,
            Priority priority,
            Money finalPrice,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return new Price(brandId, productId, priceListId, priority, finalPrice, startDate, endDate);
    }

    public boolean appliesAt(LocalDateTime applicationDate) {
        Validator.create().requireNonNull(applicationDate, "applicationDate").throwIfInvalid();

        return !applicationDate.isBefore(startDate) && !applicationDate.isAfter(endDate);
    }
}
