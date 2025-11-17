package com.example.cryptoexchange;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crypto-exchange")
public class CryptoExchangeController {

    private final CryptoExchangeService service;

    public CryptoExchangeController(CryptoExchangeService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CryptoExchangeModel>> getAllExchanges() {
        return ResponseEntity.ok(service.getAllExchanges());
    }

    @GetMapping("/pair")
    public ResponseEntity<CryptoExchangeModel> getExchange(
            @RequestParam String fromCurrency,
            @RequestParam String toCurrency) {
        return ResponseEntity.ok(service.getExchange(fromCurrency, toCurrency));
    }

    @PostMapping
    public ResponseEntity<CryptoExchangeModel> createExchange(@RequestBody CryptoExchangeModel model) {
        return ResponseEntity.status(201).body(service.createExchange(model));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CryptoExchangeModel> updateExchange(
            @PathVariable Long id,
            @RequestBody CryptoExchangeModel model) {
        return ResponseEntity.ok(service.updateExchange(id, model));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExchange(@PathVariable Long id) {
        service.deleteExchange(id);
        return ResponseEntity.noContent().build();
    }
}
