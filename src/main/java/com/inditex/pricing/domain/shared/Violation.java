package com.inditex.pricing.domain.shared;

public record Violation(String field, String message) {

    public Violation {
        if (field == null || field.isBlank()) {
            throw new IllegalArgumentException("field must not be blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message must not be blank");
        }
    }

    public static Violation of(String field, String message) {
        return new Violation(field, message);
    }
}
