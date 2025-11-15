package com.example.bankaccount;

import java.math.BigDecimal;

public class BankAccountDto {
    public String email;
    public BigDecimal eur;
    public BigDecimal usd;
    public BigDecimal gbp;
    public BigDecimal chf;
    public BigDecimal rsd;

    public BankAccountDto() {}

    public BankAccountDto(String email) {
        this.email = email;
        this.eur = BigDecimal.ZERO;
        this.usd = BigDecimal.ZERO;
        this.gbp = BigDecimal.ZERO;
        this.chf = BigDecimal.ZERO;
        this.rsd = BigDecimal.ZERO;
    }
}
