package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.UserDto;

@FeignClient(name = "bank-account", url = "http://localhost:8200", path = "/api/bank-accounts")
public interface BankAccountProxy {
	
	@GetMapping("/api/users/email")
    UserDto getUserByEmail(@RequestParam String email);
	
    @PostMapping("/createForUser")
    void createForUser(@RequestParam String email);
    
    @DeleteMapping("/bankaccounts/{email}")
    void deleteByEmail(@PathVariable String email);

}
