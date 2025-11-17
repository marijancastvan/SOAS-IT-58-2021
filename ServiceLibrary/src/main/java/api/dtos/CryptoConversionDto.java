package api.dtos;

import java.math.BigDecimal;

public class CryptoConversionDto {
    public String email;        // korisnik
    public String fromCurrency; // valuta koja se šalje
    public String toCurrency;   // valuta koja se prima
    public BigDecimal amount;   // količina koja se šalje

    public CryptoConversionDto() {}

    public CryptoConversionDto(String email, String fromCurrency, String toCurrency, BigDecimal amount) {
        this.email = email;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }
}
