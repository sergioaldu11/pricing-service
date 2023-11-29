package com.inditex.pricingservice.controller;

import com.inditex.pricingservice.dto.OutputDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

public interface PriceController {

    @GetMapping(value = "/getPrice")
    ResponseEntity<OutputDTO> getPrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId);
}
