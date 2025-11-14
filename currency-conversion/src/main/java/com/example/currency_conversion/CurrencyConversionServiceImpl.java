package com.example.currency_conversion;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.Util.exceptions.InvalidQuantityException;

import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.proxies.CurrencyExchangeProxy;
import api.services.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;

import org.springframework.stereotype.Service;

@Service
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final RestTemplate template = new RestTemplate();

    @Autowired
    private CurrencyExchangeProxy proxy;

    private final Retry retry;
    private CurrencyExchangeDto response;

    public CurrencyConversionServiceImpl(RetryRegistry registry) {
        this.retry = registry.retry("default");
    }

    @Override
    @CircuitBreaker(name = "cb", fallbackMethod = "fallback")
    public ResponseEntity<?> getConversionFeign(String from, String to, BigDecimal quantity) {
        validateParameters(from, to, quantity);

        retry.executeSupplier(() -> response = proxy.getExchangeFeign(from, to));

        CurrencyConversionDto finalResponse = new CurrencyConversionDto(response, quantity);
        finalResponse.setFeign(true);

        return ResponseEntity.ok(finalResponse);
    }

    @Override
    public ResponseEntity<?> getConversion(String from, String to, BigDecimal quantity) {
        validateParameters(from, to, quantity);

        String endpoint = "http://localhost:8000/currency-exchange?from=" + from + "&to=" + to;

        ResponseEntity<CurrencyExchangeDto> exchangeResponse =
                template.getForEntity(endpoint, CurrencyExchangeDto.class);

        return ResponseEntity.ok(new CurrencyConversionDto(exchangeResponse.getBody(), quantity));
    }

    private void validateParameters(String from, String to, BigDecimal quantity) {
        if (from == null || from.isBlank() || to == null || to.isBlank())
            throw new IllegalArgumentException("Currency parameters cannot be empty");

        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidQuantityException("Quantity must be positive");

        if (quantity.compareTo(BigDecimal.valueOf(300)) > 0)
            throw new InvalidQuantityException("Quantity exceeds 300");
    }

    public ResponseEntity<?> fallback(Throwable ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Currency conversion service temporarily unavailable (fallback), reason: " + ex.getMessage());
    }
}
