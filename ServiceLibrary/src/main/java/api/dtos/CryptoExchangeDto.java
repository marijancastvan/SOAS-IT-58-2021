package api.dtos;

import java.math.BigDecimal;

public class CryptoExchangeDto {
    public String fromCurrency;
    public String toCurrency;
    public BigDecimal rate;

    public CryptoExchangeDto() {}

    public CryptoExchangeDto(String fromCurrency, String toCurrency, BigDecimal rate) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }
}
