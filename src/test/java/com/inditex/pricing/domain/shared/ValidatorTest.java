package com.inditex.pricing.domain.shared;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ValidatorTest {
    @Test
    void aggregatesFailuresWithStableFieldNamesAndMessages() {
        var validator =
                Validator.create()
                        .requireNonNull(null, "date")
                        .requirePositive(0, "brandId")
                        .requireNonNegative(-1, "priority")
                        .requireTrue(false, "range", "invalid range");
        var exception = assertThrows(DomainValidationException.class, validator::throwIfInvalid);
        assertEquals("Domain validation failed", exception.getMessage());
        assertEquals(
                List.of(
                        new Violation("date", "must not be null"),
                        new Violation("brandId", "must be positive"),
                        new Violation("priority", "must be >= 0"),
                        new Violation("range", "invalid range")),
                exception.violations());
        assertThrows(UnsupportedOperationException.class, () -> exception.violations().clear());
    }

    @Test
    void acceptsValidValuesAndAnEmptyValidation() {
        assertDoesNotThrow(
                () ->
                        Validator.create()
                                .requireNonNull("date", "date")
                                .requirePositive(1, "brandId")
                                .requireNonNegative(0, "priority")
                                .requireTrue(true, "range", "invalid")
                                .throwIfInvalid());
        assertDoesNotThrow(() -> Validator.create().throwIfInvalid());
    }

    @Test
    void exceptionSnapshotsViolations() {
        var source = new ArrayList<>(List.of(Violation.of("id", "invalid")));
        var exception = new DomainValidationException(source);
        source.clear();
        assertEquals(List.of(Violation.of("id", "invalid")), exception.violations());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void rejectsMissingViolationMetadata(String value) {
        assertThrows(IllegalArgumentException.class, () -> new Violation(value, "invalid"));
        assertThrows(IllegalArgumentException.class, () -> new Violation("id", value));
        assertThrows(IllegalArgumentException.class, () -> Violation.of(value, "invalid"));
        assertThrows(IllegalArgumentException.class, () -> Violation.of("id", value));
    }
}
