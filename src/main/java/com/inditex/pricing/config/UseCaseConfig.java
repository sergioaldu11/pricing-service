package com.inditex.pricing.config;

import com.inditex.pricing.application.ports.in.GetApplicablePriceUseCase;
import com.inditex.pricing.application.ports.out.PriceRepositoryPort;
import com.inditex.pricing.application.usecase.GetApplicablePriceService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GetApplicablePriceUseCase getApplicablePriceUseCase(PriceRepositoryPort repositoryPort) {
        return new GetApplicablePriceService(repositoryPort);
    }
}
