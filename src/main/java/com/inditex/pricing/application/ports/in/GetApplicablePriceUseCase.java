package com.inditex.pricing.application.ports.in;

import com.inditex.pricing.domain.price.Price;
import java.util.Optional;

public interface GetApplicablePriceUseCase {
    Optional<Price> execute(GetApplicablePriceQuery query);
}
