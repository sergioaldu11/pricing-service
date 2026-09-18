package com.inditex.pricing.adapters.out.persistence.price;

import com.inditex.pricing.domain.price.*;
import java.util.Currency;

final class PriceEntityMapper {

    Price toDomain(PriceEntity e) {
        return Price.create(
                BrandId.of(e.getBrandId()),
                ProductId.of(e.getProductId()),
                PriceListId.of(e.getPriceList()),
                Priority.of(e.getPriority()),
                Money.of(e.getPrice(), Currency.getInstance(e.getCurrency())),
                e.getStartDate(),
                e.getEndDate());
    }
}
