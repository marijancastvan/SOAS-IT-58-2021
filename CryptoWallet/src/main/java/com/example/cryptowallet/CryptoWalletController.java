package com.example.cryptowallet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.CryptoWalletDto;

import java.util.List;

@RestController
@RequestMapping("/api/crypto-wallets")
public class CryptoWalletController {

    private final CryptoWalletService service;

    public CryptoWalletController(CryptoWalletService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<CryptoWalletModel>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/email")
    public ResponseEntity<CryptoWalletModel> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @PostMapping("/createForUser")
    public ResponseEntity<?> createForUser(@RequestParam String email) {
        return ResponseEntity.status(201).body(service.createForUser(email));
    }

    @PutMapping("/email")
    public ResponseEntity<?> update(@RequestParam String email, @RequestBody CryptoWalletDto dto) {
        return ResponseEntity.ok(service.update(email, dto));
    }

    @DeleteMapping("/email")
    public ResponseEntity<?> delete(@RequestParam String email) {
        service.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
