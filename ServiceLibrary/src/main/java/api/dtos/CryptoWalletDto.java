package api.dtos;

import java.math.BigDecimal;

public class CryptoWalletDto {
    public String email;
    public String role; 
    public BigDecimal btc;
    public BigDecimal eth;
    public BigDecimal ada;

    public CryptoWalletDto() {
        this.btc = BigDecimal.ZERO;
        this.eth = BigDecimal.ZERO;
        this.ada = BigDecimal.ZERO;
    }

    public CryptoWalletDto(String email) {
        this.email = email;
        this.role = role;
        this.btc = BigDecimal.ZERO;
        this.eth = BigDecimal.ZERO;
        this.ada = BigDecimal.ZERO;
    }

    // Dohvati stanje određene valute
    public BigDecimal getAmount(String currency) {
        return switch (currency.toUpperCase()) {
            case "BTC" -> btc;
            case "ETH" -> eth;
            case "ADA" -> ada;
            default -> BigDecimal.ZERO;
        };
    }

    // Postavi stanje određene valute
    public void setAmount(String currency, BigDecimal amount) {
        switch (currency.toUpperCase()) {
            case "BTC" -> btc = amount;
            case "ETH" -> eth = amount;
            case "ADA" -> ada = amount;
        }
    }

    // Dodaj ili oduzmi količinu određene valute
    public void updateAmount(String currency, BigDecimal delta) {
        setAmount(currency, getAmount(currency).add(delta));
    }
}
