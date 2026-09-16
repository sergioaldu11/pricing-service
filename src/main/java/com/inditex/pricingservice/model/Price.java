package com.inditex.pricingservice.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "prices")
@Data
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private Long brandId;

    @NotBlank
    @Column(nullable = false)
    private LocalDateTime startDate;

    @NotBlank
    @Column(nullable = false)
    private LocalDateTime endDate;

    @NotBlank
    @Column(nullable = false)
    private Integer priceList;

    @NotBlank
    @Column(nullable = false)
    private Long productId;

    @NotBlank
    @Column(nullable = false)
    private Integer priority;

    @Positive
    @Column(nullable = false)
    private BigDecimal price;

    @NotBlank
    @Column(name = "curr", length = 3, nullable = false)
    private String currency;
}