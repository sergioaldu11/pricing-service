package com.inditex.pricing.adapters.out.persistence.price;

import static org.junit.jupiter.api.Assertions.*;

import com.inditex.pricing.application.ports.out.PriceRepositoryPort;
import com.inditex.pricing.domain.price.BrandId;
import com.inditex.pricing.domain.price.ProductId;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class PriceRepositoryIT {
    private static final long BRAND = 900;
    private static final long PRODUCT = 901;
    private static final LocalDateTime START = LocalDateTime.of(2020, 6, 14, 0, 0);
    @Autowired PriceRepositoryPort repository;
    @Autowired JdbcTemplate jdbc;

    private void insert(
            long id,
            long brand,
            long product,
            int priority,
            LocalDateTime start,
            LocalDateTime end) {
        jdbc.update(
                "INSERT INTO prices (id, brand_id, product_id, price_list, priority, start_date, end_date, price, curr) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                id,
                brand,
                product,
                id,
                priority,
                start,
                end,
                new BigDecimal("12.34"),
                "EUR");
    }

    private long tariff(LocalDateTime date) {
        return repository
                .findApplicable(BrandId.of(BRAND), ProductId.of(PRODUCT), date)
                .orElseThrow()
                .priceListId()
                .value();
    }

    @Test
    void priorityPrecedesRecencyAndIgnoresIneligibleRows() {
        insert(10001, BRAND, PRODUCT, 5, START, START.plusDays(2));
        insert(10002, BRAND, PRODUCT, 4, START.plusHours(1), START.plusDays(2));
        insert(10003, BRAND, PRODUCT, 99, START.minusDays(2), START.minusSeconds(1));
        insert(10004, BRAND, PRODUCT, 99, START.plusDays(1), START.plusDays(2));
        insert(10005, BRAND + 1, PRODUCT, 99, START, START.plusDays(2));
        insert(10006, BRAND, PRODUCT + 1, 99, START, START.plusDays(2));
        assertEquals(10001, tariff(START.plusHours(2)));
    }

    @Test
    void equalPrioritiesChooseTheLatestStartThenTheLowestPersistedId() {
        insert(10001, BRAND, PRODUCT, 1, START, START.plusDays(2));
        insert(10003, BRAND, PRODUCT, 1, START.plusHours(1), START.plusDays(2));
        insert(10002, BRAND, PRODUCT, 1, START.plusHours(1), START.plusDays(2));
        assertEquals(10002, tariff(START.plusHours(2)));
    }

    @Test
    void bothBoundsAndSingleInstantIntervalsAreInclusive() {
        insert(10001, BRAND, PRODUCT, 0, START, START.plusHours(1));
        insert(10002, BRAND, PRODUCT, 1, START.plusMinutes(30), START.plusMinutes(30));
        assertEquals(10001, tariff(START));
        assertEquals(10001, tariff(START.plusHours(1)));
        assertEquals(10002, tariff(START.plusMinutes(30)));
        assertTrue(
                repository
                        .findApplicable(
                                BrandId.of(BRAND), ProductId.of(PRODUCT), START.minusNanos(1))
                        .isEmpty());
        assertTrue(
                repository
                        .findApplicable(
                                BrandId.of(BRAND),
                                ProductId.of(PRODUCT),
                                START.plusHours(1).plusNanos(1))
                        .isEmpty());
    }

    @Test
    void selectsTheSameWinnerAsAnIndependentOracleAcrossGeneratedOverlaps() {
        Random random = new Random(35455);
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            long start = random.nextLong(100);
            Row row = new Row(20000 + i, random.nextInt(4), start, start + random.nextLong(1, 50));
            rows.add(row);
            insert(
                    row.id(),
                    BRAND,
                    PRODUCT,
                    row.priority(),
                    START.plusSeconds(row.start()),
                    START.plusSeconds(row.end()));
        }
        for (long second = -1; second <= 150; second++) {
            Row winner = null;
            for (Row row : rows) {
                if (second < row.start() || second > row.end()) continue;
                if (winner == null
                        || row.priority() > winner.priority()
                        || (row.priority() == winner.priority() && row.start() > winner.start())
                        || (row.priority() == winner.priority()
                                && row.start() == winner.start()
                                && row.id() < winner.id())) {
                    winner = row;
                }
            }
            var actual =
                    repository.findApplicable(
                            BrandId.of(BRAND), ProductId.of(PRODUCT), START.plusSeconds(second));
            assertEquals(
                    winner == null ? null : winner.id(),
                    actual.map(p -> p.priceListId().value()).orElse(null),
                    "Query at second " + second);
        }
    }

    @ParameterizedTest
    @CsvSource({
        "0,1,1,0,1.00,1",
        "1,0,1,0,1.00,1",
        "1,1,0,0,1.00,1",
        "1,1,1,-1,1.00,1",
        "1,1,1,0,-0.01,1",
        "1,1,1,0,1.00,-1"
    })
    void databaseRejectsInvalidRows(
            long brand, long product, long tariff, int priority, BigDecimal amount, int duration) {
        assertThrows(
                DataIntegrityViolationException.class,
                () ->
                        jdbc.update(
                                "INSERT INTO prices (brand_id, product_id, price_list, priority, start_date, end_date, price, curr) "
                                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                                brand,
                                product,
                                tariff,
                                priority,
                                START,
                                START.plusSeconds(duration),
                                amount,
                                "EUR"));
    }

    record Row(long id, int priority, long start, long end) {}
}
