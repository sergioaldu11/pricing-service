package com.inditex.pricing.adapters.in.rest.price;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.inditex.pricing.adapters.in.rest.error.ApiExceptionHandler;
import com.inditex.pricing.application.ports.in.GetApplicablePriceQuery;
import com.inditex.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.inditex.pricing.domain.price.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class PriceControllerTest {
    @Test
    void validationErrorsIdentifyAllInvalidParameters() throws Exception {
        mvc(query -> {
                    throw new AssertionError("Invalid request reached the use case");
                })
                .perform(
                        get("/api/prices")
                                .param("applicationDate", DATE)
                                .param("productId", "0")
                                .param("brandId", "-1"))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.code").value("HTTP_400"),
                        jsonPath("$.violations", hasSize(2)),
                        jsonPath(
                                "$.violations[*].field",
                                containsInAnyOrder("productId", "brandId")),
                        jsonPath("$.violations[*].message", everyItem(not(emptyOrNullString()))));
    }

    private static final String DATE = "2020-06-14T16:00:00";

    private static MockMvc mvc(GetApplicablePriceUseCase useCase) {
        var conversion =
                new org.springframework.format.support.DefaultFormattingConversionService();
        conversion.addConverter(new LocalDateTimeConverter());
        return MockMvcBuilders.standaloneSetup(new PriceController(useCase))
                .setConversionService(conversion)
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void mapsEveryResponseFieldAndPassesTheExactQueryToTheUseCase() throws Exception {
        var captured = new AtomicReference<GetApplicablePriceQuery>();
        var start = LocalDateTime.parse("2020-06-14T15:00:00");
        var end = LocalDateTime.parse("2020-06-14T18:30:00");
        Price result =
                Price.create(
                        BrandId.of(7),
                        ProductId.of(35455000000L),
                        PriceListId.of(22),
                        Priority.of(3),
                        Money.eur(new BigDecimal("25.45")),
                        start,
                        end);
        mvc(query -> {
                    captured.set(query);
                    return Optional.of(result);
                })
                .perform(
                        get("/api/prices")
                                .param("applicationDate", DATE)
                                .param("productId", "35455000000")
                                .param("brandId", "7"))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                        jsonPath("$.productId").value(35455000000L),
                        jsonPath("$.brandId").value(7),
                        jsonPath("$.priceList").value(22),
                        jsonPath("$.price").value(25.45),
                        jsonPath("$.currency").value("EUR"),
                        jsonPath("$.startDate").value(start.toString() + ":00"),
                        jsonPath("$.endDate").value(end.toString() + ":00"),
                        jsonPath("$.*", hasSize(7)));
        assertEquals(
                GetApplicablePriceQuery.of(7, 35455000000L, LocalDateTime.parse(DATE)),
                captured.get());
    }

    @Test
    void missingPriceHasAnActionableProblemDetail() throws Exception {
        mvc(query -> Optional.empty())
                .perform(
                        get("/api/prices")
                                .param("applicationDate", DATE)
                                .param("productId", "35455")
                                .param("brandId", "1"))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.status").value(404),
                        jsonPath("$.title").value("Not Found"),
                        jsonPath("$.code").value("PRICE_NOT_FOUND"),
                        jsonPath("$.instance").value("/api/prices"),
                        jsonPath("$.detail")
                                .value(
                                        "No applicable price exists for the requested product, brand and date."),
                        jsonPath("$.type").value("urn:inditex:pricing:error:price_not_found"));
    }

    @ParameterizedTest
    @CsvSource({
        "0,35455,2020-06-14T16:00:00",
        "-1,35455,2020-06-14T16:00:00",
        "1,0,2020-06-14T16:00:00",
        "1,-1,2020-06-14T16:00:00",
        "a,35455,2020-06-14T16:00:00",
        "1,1.5,2020-06-14T16:00:00",
        "1,9223372036854775808,2020-06-14T16:00:00",
        "1,35455,bad-date",
        "1,35455,2020-02-30T10:00:00",
        "1,35455,2020-06-14"
    })
    void badInputsReturn400WithoutExecutingTheUseCase(String brand, String product, String date)
            throws Exception {
        mvc(query -> {
                    throw new AssertionError("Invalid request reached the use case");
                })
                .perform(
                        get("/api/prices")
                                .param("applicationDate", date)
                                .param("productId", product)
                                .param("brandId", brand))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON),
                        jsonPath("$.status").value(400),
                        jsonPath("$.code").value("HTTP_400"),
                        jsonPath("$.instance").value("/api/prices"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"brandId", "productId", "applicationDate"})
    void missingParametersAreClientErrors(String missing) throws Exception {
        var request = get("/api/prices");
        if (!missing.equals("brandId")) request.param("brandId", "1");
        if (!missing.equals("productId")) request.param("productId", "35455");
        if (!missing.equals("applicationDate")) request.param("applicationDate", DATE);
        mvc(query -> {
                    throw new AssertionError("Missing parameter reached use case");
                })
                .perform(request)
                .andExpectAll(status().isBadRequest(), jsonPath("$.status").value(400));
    }

    @Test
    void unexpectedFailuresDoNotExposeInternalDetails() throws Exception {
        var response =
                mvc(query -> {
                            throw new IllegalStateException("jdbc:secret-password");
                        })
                        .perform(
                                get("/api/prices")
                                        .param("applicationDate", DATE)
                                        .param("productId", "35455")
                                        .param("brandId", "1"))
                        .andExpectAll(
                                status().isInternalServerError(),
                                content()
                                        .contentTypeCompatibleWith(
                                                MediaType.APPLICATION_PROBLEM_JSON),
                                jsonPath("$.status").value(500),
                                jsonPath("$.code").value("INTERNAL_ERROR"),
                                jsonPath("$.title").value("Internal Server Error"),
                                jsonPath("$.type")
                                        .value("urn:inditex:pricing:error:internal_error"),
                                jsonPath("$.detail")
                                        .value(
                                                "An unexpected error occurred. Contact support with the errorId."),
                                jsonPath("$.errorId").isString(),
                                header().exists("X-Error-Id"),
                                content().string(not(containsString("secret-password"))),
                                jsonPath("$.trace").doesNotExist())
                        .andReturn()
                        .getResponse();
        assertTrue(response.getContentAsString().contains(response.getHeader("X-Error-Id")));
    }

    @Test
    void unsupportedMethodHasAProblemAndPreservesTheAllowHeader() throws Exception {
        mvc(query -> Optional.empty())
                .perform(post("/api/prices"))
                .andExpectAll(
                        status().isMethodNotAllowed(),
                        header().string("Allow", containsString("GET")),
                        jsonPath("$.status").value(405),
                        jsonPath("$.code").value("HTTP_405"));
    }
}
