package api.dtos;

import java.math.BigDecimal;

public class CryptoConversionDto {
    public String email;        // korisnik
    private String role;
    public String fromCurrency; // valuta koja se šalje
    public String toCurrency;   // valuta koja se prima
    public BigDecimal amount;   // količina koja se šalje

    public CryptoConversionDto() {}

    public CryptoConversionDto(String email, String role, String fromCurrency, String toCurrency, BigDecimal amount) {
        this.email = email;
        this.role = role;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.amount = amount;
    }
}
