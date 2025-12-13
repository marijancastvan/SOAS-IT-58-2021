package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import api.dtos.BankAccountDto;

@FeignClient(name = "bank-account", url = "http://localhost:8200", path = "/api/bank-accounts")
public interface BankAccountProxy {

    @GetMapping("/email")
    BankAccountDto getByEmail(@RequestParam String email);

    @PostMapping("/createForUser")
    void createForUser(@RequestParam String email);

    @PutMapping("/email")
    void updateWallet(
            @RequestParam String email,
            @RequestBody BankAccountDto dto
    );

    @DeleteMapping("/{email}")
    void deleteByEmail(@PathVariable String email);
}
