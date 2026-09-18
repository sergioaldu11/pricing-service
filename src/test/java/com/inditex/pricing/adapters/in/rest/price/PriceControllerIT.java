package com.inditex.pricing.adapters.in.rest.price;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class PriceControllerIT {

    static final long PRODUCT_ID = 35455;
    static final long BRAND_ID = 1;

    @Autowired RestTestClient client;

    @ParameterizedTest(name = "{0}")
    @MethodSource("cases")
    void should_return_applicable_price(String name, Case c) {
        client.get()
                .uri(
                        b ->
                                b.path("/api/prices")
                                        .queryParam("applicationDate", c.applicationDate())
                                        .queryParam("productId", PRODUCT_ID)
                                        .queryParam("brandId", BRAND_ID)
                                        .build())
                .exchange()
                .expectAll(
                        r -> r.expectStatus().isOk(),
                        r ->
                                r.expectBody()
                                        .jsonPath("$.productId")
                                        .isEqualTo((int) PRODUCT_ID)
                                        .jsonPath("$.brandId")
                                        .isEqualTo((int) BRAND_ID)
                                        .jsonPath("$.priceList")
                                        .isEqualTo(c.priceList())
                                        .jsonPath("$.startDate")
                                        .isEqualTo(c.startDate())
                                        .jsonPath("$.endDate")
                                        .isEqualTo(c.endDate())
                                        .jsonPath("$.currency")
                                        .isEqualTo("EUR")
                                        .jsonPath("$.price")
                                        .value(v -> assertMoney(v, c.price())));
    }

    static void assertMoney(Object actual, String expected) {
        BigDecimal a = new BigDecimal(actual.toString()).setScale(2, RoundingMode.UNNECESSARY);
        BigDecimal e = new BigDecimal(expected).setScale(2, RoundingMode.UNNECESSARY);
        if (a.compareTo(e) != 0) throw new AssertionError("Expected price " + e + " but was " + a);
    }

    @ParameterizedTest
    @CsvSource({
        "2020-06-14T00:00:00,1", "2020-06-14T14:59:59.999999999,1",
        "2020-06-14T15:00:00,2", "2020-06-14T18:30:00,2",
        "2020-06-14T18:30:00.000000001,1", "2020-06-15T00:00:00,3",
        "2020-06-15T11:00:00,3", "2020-06-15T11:00:00.000000001,1",
        "2020-06-15T15:59:59.999999999,1", "2020-06-15T16:00:00,4",
        "2020-12-31T23:59:59,4"
    })
    void preservesInclusiveTariffBoundaries(String date, int tariff) {
        client.get()
                .uri(
                        b ->
                                b.path("/api/prices")
                                        .queryParam("applicationDate", date)
                                        .queryParam("productId", PRODUCT_ID)
                                        .queryParam("brandId", BRAND_ID)
                                        .build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody()
                .jsonPath("$.priceList")
                .isEqualTo(tariff);
    }

    @ParameterizedTest
    @CsvSource({
        "2020-06-14T10:00:00,35455,0,400", "2020-06-14T10:00:00,-1,1,400",
        "invalid,35455,1,400", "2020-06-14T10:00:00,abc,1,400",
        "2020-06-14T10:00:00,99999,1,404", "2020-06-14T10:00:00,35455,2,404",
        "2020-06-13T23:59:59.999999999,35455,1,404", "2020-12-31T23:59:59.000000001,35455,1,404"
    })
    void exposesTheErrorContractOverRealHttp(
            String date, String product, String brand, int status) {
        client.get()
                .uri(
                        b ->
                                b.path("/api/prices")
                                        .queryParam("applicationDate", date)
                                        .queryParam("productId", product)
                                        .queryParam("brandId", brand)
                                        .build())
                .exchange()
                .expectStatus()
                .isEqualTo(status)
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(status)
                .jsonPath("$.instance")
                .isEqualTo("/api/prices")
                .jsonPath("$.code")
                .isEqualTo(status == 404 ? "PRICE_NOT_FOUND" : "HTTP_400")
                .jsonPath("$.trace")
                .doesNotExist();
    }

    @Test
    void missingParametersAreRejectedByTheRunningApplication() {
        client.get()
                .uri("/api/prices")
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody()
                .jsonPath("$.status")
                .isEqualTo(400);
    }

    @ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(
            strings = {"2020-06-14T16:00:00Z", "2020-06-14T16:00:00+02:00"})
    void timeZoneOffsetsAreRejectedInsteadOfSilentlyDiscarded(String date) {
        client.get()
                .uri(
                        b ->
                                b.path("/api/prices")
                                        .queryParam("applicationDate", date)
                                        .queryParam("productId", PRODUCT_ID)
                                        .queryParam("brandId", BRAND_ID)
                                        .build())
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    static Stream<Arguments> cases() {
        return Stream.of(
                Arguments.of(
                        "Test 1: 2020-06-14 10:00",
                        new Case(
                                "2020-06-14T10:00:00",
                                1,
                                "2020-06-14T00:00:00",
                                "2020-12-31T23:59:59",
                                "35.50")),
                Arguments.of(
                        "Test 2: 2020-06-14 16:00",
                        new Case(
                                "2020-06-14T16:00:00",
                                2,
                                "2020-06-14T15:00:00",
                                "2020-06-14T18:30:00",
                                "25.45")),
                Arguments.of(
                        "Test 3: 2020-06-14 21:00",
                        new Case(
                                "2020-06-14T21:00:00",
                                1,
                                "2020-06-14T00:00:00",
                                "2020-12-31T23:59:59",
                                "35.50")),
                Arguments.of(
                        "Test 4: 2020-06-15 10:00",
                        new Case(
                                "2020-06-15T10:00:00",
                                3,
                                "2020-06-15T00:00:00",
                                "2020-06-15T11:00:00",
                                "30.50")),
                Arguments.of(
                        "Test 5: 2020-06-16 21:00",
                        new Case(
                                "2020-06-16T21:00:00",
                                4,
                                "2020-06-15T16:00:00",
                                "2020-12-31T23:59:59",
                                "38.95")));
    }

    record Case(
            String applicationDate,
            int priceList,
            String startDate,
            String endDate,
            String price) {}
}
