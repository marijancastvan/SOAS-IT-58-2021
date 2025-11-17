package api.dtos;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class BankAccountDto {

    public String email;
    public BigDecimal eur;
    public BigDecimal usd;
    public BigDecimal gbp;
    public BigDecimal chf;
    public BigDecimal rsd;

    private Map<String, BigDecimal> balances = new HashMap<>();

    public BankAccountDto() {
        this.eur = BigDecimal.ZERO;
        this.usd = BigDecimal.ZERO;
        this.gbp = BigDecimal.ZERO;
        this.chf = BigDecimal.ZERO;
        this.rsd = BigDecimal.ZERO;

        balances.put("EUR", eur);
        balances.put("USD", usd);
        balances.put("GBP", gbp);
        balances.put("CHF", chf);
        balances.put("RSD", rsd);
    }

    public BankAccountDto(String email) {
        this();
        this.email = email;
    }

    // Getter for a specific currency
    public BigDecimal getAmount(String currency) {
        return balances.getOrDefault(currency.toUpperCase(), BigDecimal.ZERO);
    }

    // Setter for a specific currency
    public void setAmount(String currency, BigDecimal amount) {
        balances.put(currency.toUpperCase(), amount);
        switch (currency.toUpperCase()) {
            case "EUR": this.eur = amount; break;
            case "USD": this.usd = amount; break;
            case "GBP": this.gbp = amount; break;
            case "CHF": this.chf = amount; break;
            case "RSD": this.rsd = amount; break;
        }
    }

    // Add or subtract from a specific currency
    public void updateAmount(String currency, BigDecimal delta) {
        BigDecimal current = getAmount(currency);
        setAmount(currency, current.add(delta));
    }
}
