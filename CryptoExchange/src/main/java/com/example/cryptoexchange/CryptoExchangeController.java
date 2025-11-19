package com.example.cryptoexchange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import api.dtos.CryptoExchangeDto;
import api.services.CryptoExchangeService;

@RestController
@RequestMapping("/api/crypto-exchange")
public class CryptoExchangeController {

    private final CryptoExchangeService service;

    public CryptoExchangeController(CryptoExchangeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CryptoExchangeDto>> getAllExchanges() {
        return ResponseEntity.ok(service.getAllExchanges());
    }

    @GetMapping("/pair")
    public ResponseEntity<CryptoExchangeDto> getExchange(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        return ResponseEntity.ok(service.getExchange(fromCurrency, toCurrency));
    }

    @PostMapping
    public ResponseEntity<CryptoExchangeDto> createExchange(@RequestBody CryptoExchangeDto dto) {
        return ResponseEntity.status(201).body(service.createExchange(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CryptoExchangeDto> updateExchange(
            @PathVariable Long id,
            @RequestBody CryptoExchangeDto dto) {
        return ResponseEntity.ok(service.updateExchange(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable Long id) {
        service.deleteExchange(id);
        return ResponseEntity.noContent().build();
    }
}
