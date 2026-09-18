package com.inditex.pricing.adapters.in.rest.price;

public final class PriceNotFoundException extends RuntimeException {
    public PriceNotFoundException() {
        super("No applicable price exists for the requested product, brand and date.");
    }
}
