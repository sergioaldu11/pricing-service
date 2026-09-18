package com.inditex.pricing.domain.shared;

import java.util.List;

public final class DomainValidationException extends RuntimeException {

    private final List<Violation> violations;

    public DomainValidationException(List<Violation> violations) {
        super("Domain validation failed");
        this.violations = List.copyOf(violations);
    }

    public List<Violation> violations() {
        return violations;
    }
}
