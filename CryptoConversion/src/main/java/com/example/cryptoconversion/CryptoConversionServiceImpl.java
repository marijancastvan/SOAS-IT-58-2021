package com.example.cryptoconversion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dtos.CryptoConversionDto;
import api.dtos.CryptoConversionResultDto;
import api.dtos.CryptoWalletDto;
import api.proxies.CryptoWalletProxy;
import api.proxies.CryptoExchangeProxy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class CryptoConversionServiceImpl implements CryptoConversionService {

    private final CryptoWalletProxy walletProxy;
    private final CryptoExchangeProxy exchangeProxy;

    public CryptoConversionServiceImpl(CryptoWalletProxy walletProxy, CryptoExchangeProxy exchangeProxy) {
        this.walletProxy = walletProxy;
        this.exchangeProxy = exchangeProxy;
    }

    @Override
    public CryptoConversionResultDto convert(CryptoConversionDto dto) {

        // Dohvati wallet korisnika
        CryptoWalletDto wallet = walletProxy.getWalletByEmail(dto.email);

        // Provera role korisnika
        String role = wallet.role;
        if("OWNER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: only USER role allowed");
        }

        // Provera da li korisnik ima dovoljno sredstava
        BigDecimal fromAmount = wallet.getAmount(dto.fromCurrency);
        if(fromAmount.compareTo(dto.amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        // Dohvati kurs razmene
        BigDecimal rate = exchangeProxy.getExchangeRate(dto.fromCurrency, dto.toCurrency).rate;

        // Izvrši konverziju
        BigDecimal convertedAmount = dto.amount.multiply(rate);
        wallet.updateAmount(dto.fromCurrency, fromAmount.subtract(dto.amount));
        wallet.updateAmount(dto.toCurrency, wallet.getAmount(dto.toCurrency).add(convertedAmount));

        // Update wallet-a
        walletProxy.updateWallet(dto.email, wallet);

        // Kreiraj poruku
        String message = String.format("Successfully converted %s: %s to %s: %s",
                dto.fromCurrency, dto.amount, dto.toCurrency, convertedAmount);

        Map<String, BigDecimal> walletState = new HashMap<>();
        walletState.put(dto.fromCurrency, wallet.getAmount(dto.fromCurrency));
        walletState.put(dto.toCurrency, wallet.getAmount(dto.toCurrency));

        return new CryptoConversionResultDto(message, walletState);
    }
}
