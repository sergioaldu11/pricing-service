package com.inditex.pricing.application.usecase;

import com.inditex.pricing.application.ports.in.GetApplicablePriceQuery;
import com.inditex.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.inditex.pricing.application.ports.out.PriceRepositoryPort;
import com.inditex.pricing.domain.price.Price;
import java.util.Objects;
import java.util.Optional;

public final class GetApplicablePriceService implements GetApplicablePriceUseCase {

    private final PriceRepositoryPort repository;

    public GetApplicablePriceService(PriceRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public Optional<Price> execute(GetApplicablePriceQuery query) {
        Objects.requireNonNull(query, "query");
        return repository.findApplicable(
                query.brandId(), query.productId(), query.applicationDate());
    }
}
