package com.inditex.pricingservice.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class OutputDTO {
    @NotBlank
    private Long productId;

    @NotBlank
    private Long brandId;

    @NotBlank
    private Integer priceList;

    @NotBlank
    private LocalDateTime startDate;

    @NotBlank
    private LocalDateTime endDate;

    @NotNull
    @Positive
    private Double price;

    @NotBlank
    private String currency;
}