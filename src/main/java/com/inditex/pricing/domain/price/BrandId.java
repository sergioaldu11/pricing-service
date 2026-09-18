package com.inditex.pricing.domain.price;

import com.inditex.pricing.domain.shared.Validator;

public record BrandId(long value) {
    public BrandId {
        Validator.create().requirePositive(value, "brandId").throwIfInvalid();
    }

    public static BrandId of(long value) {
        return new BrandId(value);
    }
}
