package com.inditex.pricing.domain.price;

import com.inditex.pricing.domain.shared.Validator;

public record PriceListId(long value) {
    public PriceListId {
        Validator.create().requirePositive(value, "priceListId").throwIfInvalid();
    }

    public static PriceListId of(long value) {
        return new PriceListId(value);
    }
}
