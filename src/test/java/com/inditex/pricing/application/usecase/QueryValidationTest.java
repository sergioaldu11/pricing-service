package com.inditex.pricing.application.usecase;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.application.ports.in.GetApplicablePriceQuery;
import com.inditex.pricing.domain.price.BrandId;
import com.inditex.pricing.domain.price.ProductId;
import com.inditex.pricing.domain.shared.DomainValidationException;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class QueryValidationTest {
    @Test
    void queryRejectsMissingArgumentsAndInvalidIdentifiers() {
        var date = LocalDateTime.of(2020, 6, 14, 10, 0);
        assertThrows(
                NullPointerException.class,
                () -> new GetApplicablePriceQuery(null, ProductId.of(1), date));
        assertThrows(
                NullPointerException.class,
                () -> new GetApplicablePriceQuery(BrandId.of(1), null, date));
        assertThrows(NullPointerException.class, () -> GetApplicablePriceQuery.of(1, 1, null));
        assertThrows(DomainValidationException.class, () -> GetApplicablePriceQuery.of(0, 1, date));
        assertThrows(DomainValidationException.class, () -> GetApplicablePriceQuery.of(1, 0, date));
    }

    @Test
    void useCaseRequiresItsDependencyAndQuery() {
        assertThrows(NullPointerException.class, () -> new GetApplicablePriceService(null));
        var service = new GetApplicablePriceService((brand, product, date) -> Optional.empty());
        assertThrows(NullPointerException.class, () -> service.execute(null));
    }
}
