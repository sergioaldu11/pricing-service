package com.inditex.pricing.domain.price;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.domain.shared.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class PriceTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 15, 0);
    private static final LocalDateTime END = START.plusHours(3).plusMinutes(30);

    private static Price price(LocalDateTime start, LocalDateTime end) {
        return new Price(
                BrandId.of(1),
                ProductId.of(35455),
                PriceListId.of(2),
                Priority.of(1),
                Money.eur(new BigDecimal("25.45")),
                start,
                end);
    }

    @Test
    void testsBothInclusiveBoundsAtNanosecondPrecision() {
        Price price = price(START, END);
        assertAll(
                () -> assertFalse(price.appliesAt(START.minusNanos(1))),
                () -> assertTrue(price.appliesAt(START)),
                () -> assertTrue(price.appliesAt(START.plusNanos(1))),
                () -> assertTrue(price.appliesAt(END.minusNanos(1))),
                () -> assertTrue(price.appliesAt(END)),
                () -> assertFalse(price.appliesAt(END.plusNanos(1))));
    }

    @Test
    void permitsAnInstantButRejectsReversedRanges() {
        assertTrue(price(START, START).appliesAt(START));
        assertThrows(DomainValidationException.class, () -> price(START, START.minusNanos(1)));
        assertThrows(DomainValidationException.class, () -> price(START, END).appliesAt(null));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 4, 5, 6})
    void everyComponentIsRequiredEvenWithThePublicConstructor(int missing) {
        Object[] values = {
            BrandId.of(1),
            ProductId.of(35455),
            PriceListId.of(2),
            Priority.of(1),
            Money.eur(new BigDecimal("25.45")),
            START,
            END
        };
        values[missing] = null;
        var exception =
                assertThrows(
                        DomainValidationException.class,
                        () ->
                                new Price(
                                        (BrandId) values[0],
                                        (ProductId) values[1],
                                        (PriceListId) values[2],
                                        (Priority) values[3],
                                        (Money) values[4],
                                        (LocalDateTime) values[5],
                                        (LocalDateTime) values[6]));
        assertEquals(1, exception.violations().size());
    }

    @Test
    void generatedIntervalsAgreeWithAnIndependentIntervalOracle() {
        Random random = new Random(35455);
        for (int i = 0; i < 300; i++) {
            long length = random.nextLong(100_000);
            long offset = random.nextLong(-10_000, 110_000);
            LocalDateTime start = START.plusDays(i);
            Price price = price(start, start.plusSeconds(length));
            assertEquals(
                    offset >= 0 && offset <= length,
                    price.appliesAt(start.plusSeconds(offset)),
                    "length=" + length + ", offset=" + offset);
        }
    }
}
