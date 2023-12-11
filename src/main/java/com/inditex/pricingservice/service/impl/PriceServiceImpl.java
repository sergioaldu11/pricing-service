package com.inditex.pricingservice.service.impl;

import com.inditex.pricingservice.dto.InputDTO;
import com.inditex.pricingservice.dto.OutputDTO;
import com.inditex.pricingservice.exception.PriceNotFoundException;
import com.inditex.pricingservice.model.Price;
import com.inditex.pricingservice.repository.PriceRepository;
import com.inditex.pricingservice.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public ResponseEntity<OutputDTO> findPrice(InputDTO inputDTO) {
        return priceRepository.findTopByBrandIdAndProductIdAndDate(
                        inputDTO.getBrandId(), inputDTO.getProductId(), inputDTO.getApplicationDate())
                .map(price -> ResponseEntity.ok(mapToOutputDTO(price)))
                .orElseThrow(() -> new PriceNotFoundException("No prices were found for the given criteria"));
    }

    private OutputDTO mapToOutputDTO(Price price) {
        return OutputDTO.builder()
                .brandId(price.getBrandId())
                .productId(price.getProductId())
                .priceList(price.getPriceList())
                .startDate(price.getStartDate())
                .endDate(price.getEndDate())
                .price(price.getPrice().doubleValue())
                .currency(price.getCurrency())
                .build();
    }
}