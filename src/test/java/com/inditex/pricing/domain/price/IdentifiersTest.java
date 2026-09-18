package com.inditex.pricing.domain.price;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.domain.shared.DomainValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IdentifiersTest {
    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1, 0})
    void rejectsNonPositiveIdentifiersThroughEveryConstructionPath(long value) {
        assertAll(
                () -> assertThrows(DomainValidationException.class, () -> new BrandId(value)),
                () -> assertThrows(DomainValidationException.class, () -> BrandId.of(value)),
                () -> assertThrows(DomainValidationException.class, () -> new ProductId(value)),
                () -> assertThrows(DomainValidationException.class, () -> ProductId.of(value)),
                () -> assertThrows(DomainValidationException.class, () -> new PriceListId(value)),
                () -> assertThrows(DomainValidationException.class, () -> PriceListId.of(value)));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 35455, Long.MAX_VALUE})
    void preservesValidIdentifiers(long value) {
        assertAll(
                () -> assertEquals(new BrandId(value), BrandId.of(value)),
                () -> assertEquals(value, BrandId.of(value).value()),
                () -> assertEquals(new ProductId(value), ProductId.of(value)),
                () -> assertEquals(value, ProductId.of(value).value()),
                () -> assertEquals(new PriceListId(value), PriceListId.of(value)),
                () -> assertEquals(value, PriceListId.of(value).value()));
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1})
    void rejectsNegativePriority(int value) {
        assertThrows(DomainValidationException.class, () -> new Priority(value));
        assertThrows(DomainValidationException.class, () -> Priority.of(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, Integer.MAX_VALUE})
    void permitsZeroAndPositivePriority(int value) {
        assertEquals(new Priority(value), Priority.of(value));
        assertEquals(value, Priority.of(value).value());
    }
}
