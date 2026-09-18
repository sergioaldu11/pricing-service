package com.inditex.pricing.adapters.in.rest.price;

import com.inditex.pricing.adapters.in.rest.price.dto.PriceResponseDto;
import com.inditex.pricing.application.ports.in.GetApplicablePriceQuery;
import com.inditex.pricing.application.ports.in.GetApplicablePriceUseCase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/prices", "/api/prices"})
public class PriceController {

    private final GetApplicablePriceUseCase useCase;
    private final PriceRestMapper mapper = new PriceRestMapper();

    public PriceController(GetApplicablePriceUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping
    public ResponseEntity<PriceResponseDto> getApplicablePrice(
            @RequestParam("applicationDate") @NotNull LocalDateTime applicationDate,
            @RequestParam("productId") @Min(1) long productId,
            @RequestParam("brandId") @Min(1) long brandId) {
        var query = GetApplicablePriceQuery.of(brandId, productId, applicationDate);

        return useCase.execute(query)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElseThrow(PriceNotFoundException::new);
    }
}
