package com.inditex.pricing.domain.price;

import com.inditex.pricing.domain.shared.Validator;

public record ProductId(long value) {
    public ProductId {
        Validator.create().requirePositive(value, "productId").throwIfInvalid();
    }

    public static ProductId of(long value) {
        return new ProductId(value);
    }
}
