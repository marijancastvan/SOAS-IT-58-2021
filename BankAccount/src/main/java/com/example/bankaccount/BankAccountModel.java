package com.example.bankaccount;

import jakarta.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_account")
public class BankAccountModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique email that links to UsersService
    @Column(nullable = false, unique = true)
    private String email;

    // amounts for fiat currencies (EUR, USD, GBP, CHF, RSD)
    @Column(nullable = false)
    private BigDecimal eur = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal usd = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal gbp = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal chf = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal rsd = BigDecimal.ZERO;

    // constructors, getters, setters
    public BankAccountModel() {}
    public BankAccountModel(String email) { this.email = email; }

    // standard getters / setters below...
    // (generate via IDE or Lombok)
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public BigDecimal getEur() { return eur; }
    public void setEur(BigDecimal eur) { this.eur = eur; }

    public BigDecimal getUsd() { return usd; }
    public void setUsd(BigDecimal usd) { this.usd = usd; }

    public BigDecimal getGbp() { return gbp; }
    public void setGbp(BigDecimal gbp) { this.gbp = gbp; }

    public BigDecimal getChf() { return chf; }
    public void setChf(BigDecimal chf) { this.chf = chf; }

    public BigDecimal getRsd() { return rsd; }
    public void setRsd(BigDecimal rsd) { this.rsd = rsd; }
}
