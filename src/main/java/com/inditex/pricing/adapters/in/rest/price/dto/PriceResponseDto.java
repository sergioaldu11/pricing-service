package com.inditex.pricing.adapters.in.rest.price.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceResponseDto(
        long productId,
        long brandId,
        long priceList,
        LocalDateTime startDate,
        LocalDateTime endDate,
        BigDecimal price,
        String currency) {}
