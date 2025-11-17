package api.dtos;

import java.math.BigDecimal;

public class TradeRequestDto {
    public String email;          // email korisnika
    public String fromCurrency;   // valuta koja se oduzima
    public String toCurrency;     // valuta koja se dobija
    public BigDecimal amount;     // iznos za razmenu

    public TradeRequestDto() {}

    public TradeRequestDto(String email, String fromCurrency, String toCurrency, BigDecimal amount) {
        this.email = email;
        this.fromCurrency = fromCurrency.toUpperCase();
        this.toCurrency = toCurrency.toUpperCase();
        this.amount = amount;
    }
}
