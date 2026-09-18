package com.inditex.pricing.domain.price;

import com.inditex.pricing.domain.shared.Validator;

public record Priority(int value) {
    public Priority {
        Validator.create().requireNonNegative(value, "priority").throwIfInvalid();
    }

    public static Priority of(int value) {
        return new Priority(value);
    }
}
