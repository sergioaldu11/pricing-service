package com.inditex.pricingservice.service.impl;

import com.inditex.pricingservice.dto.InputDTO;
import com.inditex.pricingservice.dto.OutputDTO;
import com.inditex.pricingservice.model.Price;
import com.inditex.pricingservice.repository.PriceRepository;
import com.inditex.pricingservice.service.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PriceServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    public ResponseEntity<OutputDTO> findPrice(InputDTO inputDTO) {
        Optional<Price> optionalPrice = priceRepository.findTopByBrandIdAndProductIdAndDate(
                inputDTO.getBrandId(), inputDTO.getProductId(), inputDTO.getApplicationDate());

        return optionalPrice.map(this::mapToOutputDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
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