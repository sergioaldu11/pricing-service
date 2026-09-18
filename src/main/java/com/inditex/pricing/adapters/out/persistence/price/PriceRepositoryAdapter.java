package com.inditex.pricing.adapters.out.persistence.price;

import com.inditex.pricing.application.ports.out.PriceRepositoryPort;
import com.inditex.pricing.domain.price.BrandId;
import com.inditex.pricing.domain.price.Price;
import com.inditex.pricing.domain.price.ProductId;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class PriceRepositoryAdapter implements PriceRepositoryPort {

    private final PriceJpaRepository jpaRepository;
    private final PriceEntityMapper mapper = new PriceEntityMapper();

    public PriceRepositoryAdapter(PriceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<Price> findApplicable(
            BrandId brandId, ProductId productId, LocalDateTime applicationDate) {
        return jpaRepository
                .findApplicable(
                        brandId.value(), productId.value(), applicationDate, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(mapper::toDomain);
    }
}
