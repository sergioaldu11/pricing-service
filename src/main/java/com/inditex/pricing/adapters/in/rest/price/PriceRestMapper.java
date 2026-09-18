package com.inditex.pricing.adapters.in.rest.price;

import com.inditex.pricing.adapters.in.rest.price.dto.PriceResponseDto;
import com.inditex.pricing.domain.price.Price;

final class PriceRestMapper {

    PriceResponseDto toDto(Price price) {
        return new PriceResponseDto(
                price.productId().value(),
                price.brandId().value(),
                price.priceListId().value(),
                price.startDate(),
                price.endDate(),
                price.finalPrice().amount(),
                price.finalPrice().currency().getCurrencyCode());
    }
}
