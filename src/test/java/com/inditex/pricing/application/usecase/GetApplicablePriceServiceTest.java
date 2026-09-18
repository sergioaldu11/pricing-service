package com.inditex.pricing.application.usecase;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.application.ports.in.GetApplicablePriceQuery;
import com.inditex.pricing.application.ports.out.PriceRepositoryPort;
import com.inditex.pricing.domain.price.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class GetApplicablePriceServiceTest {

    @Test
    void should_delegate_to_repository_and_return_result() {
        LocalDateTime date = LocalDateTime.of(2020, 6, 14, 10, 0);

        Price expected =
                Price.create(
                        BrandId.of(1),
                        ProductId.of(35455),
                        PriceListId.of(1),
                        Priority.of(0),
                        Money.of(BigDecimal.valueOf(35.50), Currency.getInstance("EUR")),
                        LocalDateTime.of(2020, 6, 14, 0, 0),
                        LocalDateTime.of(2020, 12, 31, 23, 59, 59));

        AtomicReference<GetApplicablePriceQuery> captured = new AtomicReference<>();

        PriceRepositoryPort repo =
                (brandId, productId, applicationDate) -> {
                    captured.set(new GetApplicablePriceQuery(brandId, productId, applicationDate));
                    return Optional.of(expected);
                };

        GetApplicablePriceService service = new GetApplicablePriceService(repo);

        Optional<Price> result = service.execute(GetApplicablePriceQuery.of(1, 35455, date));

        assertTrue(result.isPresent());
        assertEquals(expected, result.get());
        assertNotNull(captured.get());
        assertEquals(1L, captured.get().brandId().value());
        assertEquals(35455L, captured.get().productId().value());
        assertEquals(date, captured.get().applicationDate());
    }

    @Test
    void should_return_empty_when_repository_returns_empty() {
        PriceRepositoryPort repo = (brandId, productId, applicationDate) -> Optional.empty();
        GetApplicablePriceService service = new GetApplicablePriceService(repo);

        Optional<Price> result =
                service.execute(
                        GetApplicablePriceQuery.of(1, 35455, LocalDateTime.of(2020, 6, 14, 10, 0)));

        assertTrue(result.isEmpty());
    }
}
