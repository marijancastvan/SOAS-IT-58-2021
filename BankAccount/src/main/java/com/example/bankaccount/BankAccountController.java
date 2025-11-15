package com.example.bankaccount;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {

    private final BankAccountService service;

    public BankAccountController(BankAccountService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BankAccountModel>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/email")
    public ResponseEntity<BankAccountModel> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    // create account for given user email (validates user via Feign)
    @PostMapping("/createForUser")
    public ResponseEntity<?> createForUser(@RequestParam String email) {
        return ResponseEntity.status(201).body(service.createForUser(email));
    }

    @PutMapping("/email")
    public ResponseEntity<?> update(@RequestParam String email, @RequestBody BankAccountDto dto) {
        return ResponseEntity.ok(service.update(email, dto));
    }

    @DeleteMapping("/email")
    public ResponseEntity<?> delete(@RequestParam String email) {
        service.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }
}
