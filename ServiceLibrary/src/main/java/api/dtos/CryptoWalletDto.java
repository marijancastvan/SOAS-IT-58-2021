package api.dtos;

import java.math.BigDecimal;

public class CryptoWalletDto {

    public String email;
    public String role;
    public BigDecimal btc = BigDecimal.ZERO;
    public BigDecimal eth = BigDecimal.ZERO;
    public BigDecimal ada = BigDecimal.ZERO;

    public CryptoWalletDto() {}

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
