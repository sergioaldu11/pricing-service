package com.inditex.pricing.domain.price;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.domain.shared.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import org.junit.jupiter.api.Test;

class DomainValidationTest {

    @Test
    void brandId_must_be_positive() {
        DomainValidationException ex =
                assertThrows(DomainValidationException.class, () -> BrandId.of(0));
        assertEquals("brandId", ex.violations().getFirst().field());
    }

    @Test
    void money_must_not_allow_negative_amount() {
        assertThrows(
                DomainValidationException.class,
                () -> Money.of(BigDecimal.valueOf(-1), Currency.getInstance("EUR")));
    }

    @Test
    void price_date_range_must_be_valid() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 13, 0, 0);

        assertThrows(
                DomainValidationException.class,
                () ->
                        Price.create(
                                BrandId.of(1),
                                ProductId.of(35455),
                                PriceListId.of(1),
                                Priority.of(0),
                                Money.eur(BigDecimal.valueOf(35.50)),
                                start,
                                end));
    }

    @Test
    void appliesAt_is_inclusive() {
        LocalDateTime start = LocalDateTime.of(2020, 6, 14, 0, 0);
        LocalDateTime end = LocalDateTime.of(2020, 6, 14, 23, 59, 59);

        Price price =
                Price.create(
                        BrandId.of(1),
                        ProductId.of(35455),
                        PriceListId.of(1),
                        Priority.of(0),
                        Money.eur(BigDecimal.valueOf(35.50)),
                        start,
                        end);

        assertTrue(price.appliesAt(start));
        assertTrue(price.appliesAt(end));
        assertFalse(price.appliesAt(start.minusSeconds(1)));
        assertFalse(price.appliesAt(end.plusSeconds(1)));
    }
}
