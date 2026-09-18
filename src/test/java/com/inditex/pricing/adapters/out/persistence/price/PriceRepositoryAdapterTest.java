package com.inditex.pricing.adapters.out.persistence.price;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inditex.pricing.domain.price.*;
import com.inditex.pricing.domain.shared.DomainValidationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

class PriceRepositoryAdapterTest {
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 15, 0);
    private static final LocalDateTime END = START.plusHours(3);

    private static PriceEntity entity() {
        var entity = new PriceEntity();
        ReflectionTestUtils.setField(entity, "brandId", 7L);
        ReflectionTestUtils.setField(entity, "productId", 35455000000L);
        ReflectionTestUtils.setField(entity, "priceList", 22L);
        ReflectionTestUtils.setField(entity, "priority", 3);
        ReflectionTestUtils.setField(entity, "startDate", START);
        ReflectionTestUtils.setField(entity, "endDate", END);
        ReflectionTestUtils.setField(entity, "price", new BigDecimal("25.45"));
        ReflectionTestUtils.setField(entity, "currency", "USD");
        return entity;
    }

    @Test
    void restrictsTheQueryToOneRowAndMapsAllFieldsWithoutLosingPrecision() {
        var repository = mock(PriceJpaRepository.class);
        var date = START.plusHours(1);
        when(repository.findApplicable(7, 35455000000L, date, PageRequest.of(0, 1)))
                .thenReturn(List.of(entity()));
        var actual =
                new PriceRepositoryAdapter(repository)
                        .findApplicable(BrandId.of(7), ProductId.of(35455000000L), date);
        var expected =
                Price.create(
                        BrandId.of(7),
                        ProductId.of(35455000000L),
                        PriceListId.of(22),
                        Priority.of(3),
                        Money.of(new BigDecimal("25.45"), Currency.getInstance("USD")),
                        START,
                        END);
        assertEquals(expected, actual.orElseThrow());
        verify(repository).findApplicable(7, 35455000000L, date, PageRequest.of(0, 1));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void noRowMeansNoApplicablePrice() {
        var repository = mock(PriceJpaRepository.class);
        when(repository.findApplicable(1, 2, START, PageRequest.of(0, 1))).thenReturn(List.of());
        assertTrue(
                new PriceRepositoryAdapter(repository)
                        .findApplicable(BrandId.of(1), ProductId.of(2), START)
                        .isEmpty());
    }

    @Test
    void corruptPersistedAmountsAreNotSilentlyAccepted() {
        var invalid = entity();
        ReflectionTestUtils.setField(invalid, "price", new BigDecimal("-0.01"));
        assertThrows(
                DomainValidationException.class, () -> new PriceEntityMapper().toDomain(invalid));
    }
}
