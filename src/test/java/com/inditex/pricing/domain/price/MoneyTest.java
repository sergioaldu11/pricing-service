package com.inditex.pricing.domain.price;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.domain.shared.DomainValidationException;
import java.math.BigDecimal;
import java.util.Currency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {
    private static final Currency EUR = Currency.getInstance("EUR");

    @ParameterizedTest
    @CsvSource({
        "0,0.00",
        "-0.00,0.00",
        "1,1.00",
        "0.01,0.01",
        "35.5000,35.50",
        "99999999.99,99999999.99"
    })
    void normalizesExactlyWithoutLosingValue(String input, String expected) {
        Money money = new Money(new BigDecimal(input), EUR);
        assertEquals(new BigDecimal(expected), money.amount());
        assertEquals(EUR, money.currency());
        assertEquals(money, Money.of(new BigDecimal(input), EUR));
        assertEquals(money, Money.eur(new BigDecimal(input)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1", "-0.001", "1.001", "1.005", "0.00000001"})
    void rejectsNegativeOrInexactAmountsInsteadOfRounding(String amount) {
        assertThrows(DomainValidationException.class, () -> new Money(new BigDecimal(amount), EUR));
        assertThrows(DomainValidationException.class, () -> Money.eur(new BigDecimal(amount)));
    }

    @Test
    void requiresAmountAndCurrency() {
        assertThrows(DomainValidationException.class, () -> new Money(null, EUR));
        assertThrows(DomainValidationException.class, () -> new Money(BigDecimal.ONE, null));
        var exception = assertThrows(DomainValidationException.class, () -> new Money(null, null));
        assertEquals(2, exception.violations().size());
    }

    @Test
    void preservesCurrencyAndValueSemantics() {
        Money dollars = Money.of(new BigDecimal("12.50"), Currency.getInstance("USD"));
        assertEquals("USD", dollars.currency().getCurrencyCode());
        assertNotEquals(Money.eur(new BigDecimal("12.50")), dollars);
        assertEquals(Money.eur(new BigDecimal("12.5")), Money.eur(new BigDecimal("12.50")));
    }
}
