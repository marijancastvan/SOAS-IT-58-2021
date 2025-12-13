package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import api.dtos.BankAccountDto;

@FeignClient(name = "bank-account", url = "http://localhost:8200", path = "/api/bank-accounts")
public interface BankAccountProxy {

    /*@GetMapping("/email")
    BankAccountDto getByEmail(@RequestParam String email);*/
	@GetMapping("/{email}")
    BankAccountDto getByEmail(
        @PathVariable("email") String email,
        @RequestHeader("X-User-Role") String role,
        @RequestHeader("X-Requester-Email") String requesterEmail
    );

    @PostMapping("/createForUser")
    void createForUser(@RequestParam String email);

    /*@PutMapping("/email")
    void updateWallet(
            @RequestParam String email,
            @RequestBody BankAccountDto dto
    );*/
    @PutMapping("/update/{email}")
    void updateWallet(
        @PathVariable("email") String email,
        @RequestBody BankAccountDto dto,
        @RequestHeader("X-User-Role") String role,
        @RequestHeader("X-Requester-Email") String requesterEmail
    );
    
    @DeleteMapping("/{email}")
    void deleteByEmail(@PathVariable String email);
}
