package com.inditex.pricing.adapters.in.rest.price;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LocalDateTimeConverterTest {
    private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

    @ParameterizedTest
    @ValueSource(
            strings = {"2020-06-14T16:00:00", "2020-06-14T16:00:00.123456789", "2020-02-29T00:00"})
    void parsesLocalTimesExactly(String value) {
        assertEquals(LocalDateTime.parse(value), converter.convert(value));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "2020-06-14T16:00:00Z",
                "2020-06-14T16:00:00+02:00",
                "2020-06-14T16:00:00-03:00",
                "2020-02-30T00:00:00",
                "2020-06-14",
                ""
            })
    void rejectsAmbiguousOrInvalidTimes(String value) {
        assertThrows(DateTimeParseException.class, () -> converter.convert(value));
    }
}
