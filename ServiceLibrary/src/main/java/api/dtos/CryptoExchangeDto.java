package api.dtos;

import java.math.BigDecimal;

public class CryptoExchangeDto {
    public Long id;              
    public String fromCurrency;
    public String toCurrency;
    public BigDecimal rate;

    public CryptoExchangeDto() {}

    public CryptoExchangeDto(Long id, String fromCurrency, String toCurrency, BigDecimal rate) {
        this.id = id;
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.rate = rate;
    }
}
