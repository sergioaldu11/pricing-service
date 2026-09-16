package com.inditex.pricingservice.dto;


import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class InputDTO {
    @NotBlank
    private LocalDateTime applicationDate;

    @NotBlank
    private Long productId;

    @NotBlank
    private Long brandId;
}