package api.dtos;

import java.math.BigDecimal;
import java.util.Map;

public class CryptoConversionResultDto {
    public String message;               // opis rezultata transakcije
    public Map<String, BigDecimal> wallet; // stanje wallet-a nakon razmene

    public CryptoConversionResultDto() {}

    public CryptoConversionResultDto(String message, Map<String, BigDecimal> wallet) {
        this.message = message;
        this.wallet = wallet;
    }
}
