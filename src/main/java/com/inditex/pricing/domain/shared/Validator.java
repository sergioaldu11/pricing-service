package com.inditex.pricing.domain.shared;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Validator {

    private final List<Violation> violations = new ArrayList<>();

    private Validator() {}

    public static Validator create() {
        return new Validator();
    }

    public Validator requireNonNull(Object value, String field) {
        if (Objects.isNull(value)) {
            violations.add(Violation.of(field, "must not be null"));
        }
        return this;
    }

    public Validator requirePositive(long value, String field) {
        if (value <= 0) {
            violations.add(Violation.of(field, "must be positive"));
        }
        return this;
    }

    public Validator requireNonNegative(int value, String field) {
        if (value < 0) {
            violations.add(Violation.of(field, "must be >= 0"));
        }
        return this;
    }

    public Validator requireTrue(boolean condition, String field, String message) {
        if (!condition) {
            violations.add(Violation.of(field, message));
        }
        return this;
    }

    public void throwIfInvalid() {
        if (!violations.isEmpty()) {
            throw new DomainValidationException(violations);
        }
    }
}
