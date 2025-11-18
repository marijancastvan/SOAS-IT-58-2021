package com.example.cryptowallet;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import api.dtos.CryptoWalletDto;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/wallet")
public class CryptoWalletController {

    private final CryptoWalletService service;

    public CryptoWalletController(CryptoWalletService service) {
        this.service = service;
    }

    // GET all wallets (ADMIN only)
    @GetMapping("/all")
    public ResponseEntity<List<CryptoWalletDto>> getAll(@RequestHeader("X-User-Role") String userRole) {
        return ResponseEntity.ok(service.getAll(userRole));
    }

    // GET wallet by email
    @GetMapping("/{email}")
    public ResponseEntity<CryptoWalletDto> getByEmail(
            @PathVariable String email,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-Requester-Email") String requesterEmail) {

        return ResponseEntity.ok(service.getByEmail(email, userRole, requesterEmail));
    }

    // CREATE wallet (ADMIN or system triggered for new USER)
    @PostMapping("/create")
    public ResponseEntity<CryptoWalletDto> create(@RequestParam String email) {
        return ResponseEntity.status(201).body(service.createForUser(email));
    }

    // UPDATE wallet
    @PutMapping("/update/{email}")
    public ResponseEntity<CryptoWalletDto> update(
            @PathVariable String email,
            @RequestBody CryptoWalletDto dto,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-Requester-Email") String requesterEmail) {

        return ResponseEntity.ok(service.update(email, dto, userRole, requesterEmail));
    }

    // DELETE wallet
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> delete(@PathVariable String email) {
        service.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // DEPOSIT funds
    @PostMapping("/deposit")
    public ResponseEntity<CryptoWalletDto> deposit(
            @RequestParam String email,
            @RequestParam String currency,
            @RequestParam BigDecimal amount,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-Requester-Email") String requesterEmail) {

        return ResponseEntity.ok(service.deposit(email, currency, amount, userRole, requesterEmail));
    }

    // WITHDRAW funds
    @PostMapping("/withdraw")
    public ResponseEntity<CryptoWalletDto> withdraw(
            @RequestParam String email,
            @RequestParam String currency,
            @RequestParam BigDecimal amount,
            @RequestHeader("X-User-Role") String userRole,
            @RequestHeader("X-Requester-Email") String requesterEmail) {

        return ResponseEntity.ok(service.withdraw(email, currency, amount, userRole, requesterEmail));
    }
}
