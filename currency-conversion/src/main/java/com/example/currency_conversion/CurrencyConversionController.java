package com.example.currency_conversion;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.services.CurrencyConversionService;

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyConversionService service;

    @GetMapping("/currency-conversion")
    public ResponseEntity<?> convert(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity) {

        return service.getConversion(from, to, quantity);
    }

    @GetMapping("/currency-conversion-feign")
    public ResponseEntity<?> convertFeign(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal quantity) {

        return service.getConversionFeign(from, to, quantity);
    }
}
