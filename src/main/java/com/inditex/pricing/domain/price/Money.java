package com.inditex.pricing.domain.price;

import com.inditex.pricing.domain.shared.Validator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Validator.create()
                .requireNonNull(amount, "amount")
                .requireNonNull(currency, "currency")
                .throwIfInvalid();

        Validator.create()
                .requireTrue(amount.signum() >= 0, "amount", "must be >= 0")
                .throwIfInvalid();

        Validator.create()
                .requireTrue(
                        amount.stripTrailingZeros().scale() <= 2,
                        "amount",
                        "must have at most two decimal places")
                .throwIfInvalid();
        amount = amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money eur(BigDecimal amount) {
        return of(amount, Currency.getInstance("EUR"));
    }
}
