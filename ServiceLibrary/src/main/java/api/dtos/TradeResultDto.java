package api.dtos;

import java.math.BigDecimal;
import java.util.Map;

public class TradeResultDto {
    public String message;                    // opis rezultata transakcije
    public Map<String, BigDecimal> wallet;   // stanje wallet-a nakon transakcije
    public Map<String, BigDecimal> bank;     // stanje bank računa nakon transakcije

    public TradeResultDto() {}

    public TradeResultDto(String message, Map<String, BigDecimal> wallet, Map<String, BigDecimal> bank) {
        this.message = message;
        this.wallet = wallet;
        this.bank = bank;
    }
}
