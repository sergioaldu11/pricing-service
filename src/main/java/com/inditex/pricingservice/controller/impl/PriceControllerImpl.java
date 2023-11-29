package com.inditex.pricingservice.controller.impl;

import com.inditex.pricingservice.controller.PriceController;
import com.inditex.pricingservice.dto.InputDTO;
import com.inditex.pricingservice.dto.OutputDTO;
import com.inditex.pricingservice.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class PriceControllerImpl implements PriceController {

    @Autowired
    private PriceService priceService;

    @GetMapping(value = "/getPrice")
    public ResponseEntity<OutputDTO> getPrice(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime applicationDate,
            @RequestParam Long productId,
            @RequestParam Long brandId) {
        return priceService.findPrice(InputDTO.builder()
                .applicationDate(applicationDate)
                .productId(productId)
                .brandId(brandId)
                .build());
    }
}