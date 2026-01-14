package api.proxies;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import api.dtos.BankAccountDto;

@FeignClient(name = "bank-account-service")
public interface BankAccountProxy {

    
    //SECOND PROXY
    @DeleteMapping("/bank-accounts/{email}")
    ResponseEntity<String> deleteBankAccount(
        @PathVariable (value ="email")String email,
        @RequestHeader("X-Internal-Call") String internalCallHeader
    );
    
    @PostMapping("/bank-accounts")
    ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto);
    
    @PutMapping("/bank-accounts/new-email")
    ResponseEntity<?> updateEmail( @RequestParam(value ="oldEmail") String oldEmail,  @RequestParam(value ="newEmail") String newEmail);
    
    @GetMapping("/bank-account/{email}/{currencyFrom}")
   	public BigDecimal getUserCurrencyAmount(@PathVariable(value="email") String email, @PathVariable(value="currencyFrom") String currencyFrom);
    
    @GetMapping("/bank-account/user")
	BankAccountDto getBankAccountForUser(@RequestHeader("Authorization") String authorizationHeader);
    
    @PutMapping("/bank-accounts/{email}")
    ResponseEntity<String> updateBankAccount(@PathVariable(value ="email") String email, @RequestBody BankAccountDto dto);

}
