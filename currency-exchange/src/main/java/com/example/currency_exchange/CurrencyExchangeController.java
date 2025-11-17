package com.example.currency_exchange;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.CurrencyExchangeDto;

@RestController
@RequestMapping("/api/currency-exchange")
public class CurrencyExchangeController {

    @Autowired
    private CurrencyExchangeServiceImpl service;

    @Autowired
    private Environment environment;

    @GetMapping
    public ResponseEntity<CurrencyExchangeDto> getCurrencyExchange(
            @RequestParam String from,
            @RequestParam String to) {

        CurrencyExchangeDto dto = service.getCurrencyExchange(from, to);
        dto.setPort(environment.getProperty("local.server.port"));
        return ResponseEntity.ok(dto);
    }
}
