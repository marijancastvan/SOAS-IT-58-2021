package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoWalletDto;
import java.util.List;

@FeignClient(name = "crypto-wallet", url = "http://localhost:8300", path = "/api/wallet")
public interface CryptoWalletProxy {

    // Dohvati wallet po korisničkom email-u
    @GetMapping("/email")
    CryptoWalletDto getWalletByEmail(@RequestParam String email);

    // Dohvati sve wallet-e
    @GetMapping("/all")
    List<CryptoWalletDto> getAllWallets();
    
    // Ažuriraj količinu u wallet-u
    @PostMapping("/update")
    void updateWallet(@RequestParam String email, @RequestBody CryptoWalletDto dto);

    // Automatsko kreiranje wallet-a za novog korisnika
    @PostMapping("/create")
    void createWalletForNewUser(@RequestParam String email);
}
