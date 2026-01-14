package api.dtos;

import java.math.BigDecimal;
import java.util.List;

public class CryptoWalletDto {

	private List<CryptoValuesDto> values;
	
	private String email;
    
    public String role;
    public BigDecimal btc = BigDecimal.ZERO;
    public BigDecimal eth = BigDecimal.ZERO;
    public BigDecimal ada = BigDecimal.ZERO;

    public CryptoWalletDto() {}
    
    public CryptoWalletDto(List<CryptoValuesDto> values, String email) {
		super();
		this.values = values;
		this.email = email;
	}
    
    public List<CryptoValuesDto> getValues() {
		return values;
	}

	public void setValues(List<CryptoValuesDto> values) {
		this.values = values;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

    // updateAmount 
    public void updateAmount(String currency, BigDecimal newValue) {
        setAmount(currency, newValue);
    }

}
