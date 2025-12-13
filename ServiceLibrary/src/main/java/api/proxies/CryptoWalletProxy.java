package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;
import java.util.List;

@FeignClient(name = "crypto-wallet", url = "http://localhost:8300", path = "/api/wallet")
public interface CryptoWalletProxy {

	@GetMapping("/{email}")
    CryptoWalletDto getWalletByEmail(@PathVariable("email") String email);

    @GetMapping("/all")
    List<CryptoWalletDto> getAllWallets();

    @PutMapping("/update/{email}")
    void updateWallet(@PathVariable String email, @RequestBody CryptoWalletDto dto);

    @PostMapping("/create")
    void createWalletForNewUser(@RequestParam String email);
}
