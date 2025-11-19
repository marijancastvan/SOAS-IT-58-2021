package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.CryptoExchangeDto;

import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "crypto-exchange", url = "http://localhost:8400", path = "/api/crypto-exchange")
public interface CryptoExchangeProxy {

    // Dohvati kurs između dve valute
	@GetMapping("/rate")
    CryptoExchangeDto getExchangeRate(@RequestParam String fromCurrency,
                                      @RequestParam String toCurrency);

    default BigDecimal getRate(String fromCurrency, String toCurrency) {
        CryptoExchangeDto dto = getExchangeRate(fromCurrency, toCurrency);
        return dto.rate;
    }
    
    // Dohvati sve kurseve
    @GetMapping("/all")
    List<CryptoExchangeDto> getAllRates();
}
