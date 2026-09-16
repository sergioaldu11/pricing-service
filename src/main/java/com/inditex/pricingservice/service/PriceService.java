package com.inditex.pricingservice.service;

import com.inditex.pricingservice.dto.InputDTO;
import com.inditex.pricingservice.dto.OutputDTO;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

public interface PriceService {
    ResponseEntity<OutputDTO> findPrice(@Valid InputDTO inputDTO);
}
