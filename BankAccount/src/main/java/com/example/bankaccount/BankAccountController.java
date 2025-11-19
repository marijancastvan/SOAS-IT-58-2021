package com.example.bankaccount;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.BankAccountDto;
import api.dtos.UserDto;
import api.services.BankAccountService;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
public class BankAccountController {

    private final BankAccountService service;

    public BankAccountController(BankAccountService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<BankAccountDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/email")
    public ResponseEntity<BankAccountDto> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(service.getByEmail(email));
    }

    @PostMapping("/createForUser")
    public ResponseEntity<BankAccountDto> createForUser(@RequestParam String email) {
        return ResponseEntity.status(201).body(service.createForUser(email));
    }

    @PutMapping("/email")
    public ResponseEntity<BankAccountDto> update(@RequestParam String email, @RequestBody BankAccountDto dto) {
        return ResponseEntity.ok(service.update(email, dto));
    }

    @DeleteMapping("/email")
    public ResponseEntity<Void> delete(@RequestParam String email) {
        service.deleteByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // Optional role-based access check
    private void checkAccess(String email, UserDto user) {
        if ("OWNER".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("OWNER not allowed");
        }
        if ("USER".equalsIgnoreCase(user.getRole()) &&
            !user.getEmail().equalsIgnoreCase(email)) {
            throw new RuntimeException("Users can access only their own account");
        }
    }
}
