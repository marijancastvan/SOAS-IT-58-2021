package com.example.cryptowallet;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.io.Serializable;

@Entity
@Table(name = "crypto_wallet")
public class CryptoWalletModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    
    private String role;

    @Column(nullable = false)
    private BigDecimal btc = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal eth = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal ada = BigDecimal.ZERO;

    // getters & setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }          
    public void setRole(String role) { this.role = role; } 
    public BigDecimal getBtc() { return btc; }
    public void setBtc(BigDecimal btc) { this.btc = btc; }
    public BigDecimal getEth() { return eth; }
    public void setEth(BigDecimal eth) { this.eth = eth; }
    public BigDecimal getAda() { return ada; }
    public void setAda(BigDecimal ada) { this.ada = ada; }
}
