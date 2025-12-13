package com.example.cryptoconversion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dtos.CryptoConversionDto;
import api.dtos.CryptoConversionResultDto;
import api.dtos.CryptoWalletDto;
import api.proxies.CryptoWalletProxy;
import api.services.CryptoConversionService;
import api.proxies.CryptoExchangeProxy;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        //CryptoWalletDto wallet = walletProxy.getWalletByEmail(dto.email);
    	CryptoWalletDto wallet = walletProxy.getWalletByEmail(
    	        dto.email,
    	        "USER",
    	        dto.email
    	);

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

        BigDecimal convertedAmount;

        boolean fromIsCrypto = !isFiat(dto.fromCurrency);
        boolean toIsCrypto = !isFiat(dto.toCurrency);

        if(fromIsCrypto && toIsCrypto) {
            // Crypto -> Crypto: koristimo USD kao međukorak
            String intermediate = "USD";
            BigDecimal rateToUSD = exchangeProxy.getRate(dto.fromCurrency, intermediate);
            BigDecimal intermediateAmount = dto.amount.multiply(rateToUSD);
            BigDecimal rateUSDToTarget = exchangeProxy.getRate(intermediate, dto.toCurrency);
            convertedAmount = intermediateAmount.multiply(rateUSDToTarget);
        } else {
            // Crypto -> Fiat ili Fiat -> Crypto
            String intermediate = dto.fromCurrency;
            if(!isFiat(dto.fromCurrency) && !isFiat(dto.toCurrency)) {
                intermediate = "USD";
            } else if(isFiat(dto.fromCurrency) && !isFiat(dto.toCurrency)) {
                // Fiat -> Crypto: ako fiat nije USD/EUR, koristi USD/EUR kao intermediate
                if(!dto.fromCurrency.equalsIgnoreCase("USD") && !dto.fromCurrency.equalsIgnoreCase("EUR")) {
                    intermediate = "USD";
                }
            } else if(!isFiat(dto.fromCurrency) && isFiat(dto.toCurrency)) {
                // Crypto -> Fiat: ako fiat nije USD/EUR, koristi USD/EUR kao intermediate
                if(!dto.toCurrency.equalsIgnoreCase("USD") && !dto.toCurrency.equalsIgnoreCase("EUR")) {
                    intermediate = "USD";
                }
            }

            BigDecimal rateFrom = exchangeProxy.getRate(dto.fromCurrency, intermediate);
            BigDecimal intermediateAmount = dto.amount.multiply(rateFrom);
            BigDecimal rateTo = exchangeProxy.getRate(intermediate, dto.toCurrency);
            convertedAmount = intermediateAmount.multiply(rateTo);
        }

        // Rounding na 8 decimala za crypto preciznost
        convertedAmount = convertedAmount.setScale(8, RoundingMode.HALF_UP);

        // Update wallet
        wallet.updateAmount(dto.fromCurrency, fromAmount.subtract(dto.amount));
        wallet.updateAmount(dto.toCurrency, wallet.getAmount(dto.toCurrency).add(convertedAmount));
        //walletProxy.updateWallet(dto.email, wallet);
        walletProxy.updateWallet(
                dto.email,
                wallet,
                "USER",
                dto.email
        );


        // Kreiraj poruku
        String message = String.format("Successfully converted %s: %s to %s: %s",
                dto.fromCurrency, dto.amount, dto.toCurrency, convertedAmount);

        Map<String, BigDecimal> walletState = new HashMap<>();
        walletState.put(dto.fromCurrency, wallet.getAmount(dto.fromCurrency));
        walletState.put(dto.toCurrency, wallet.getAmount(dto.toCurrency));

        return new CryptoConversionResultDto(message, walletState);
    }

    private boolean isFiat(String currency) {
        return currency.equalsIgnoreCase("USD") ||
               currency.equalsIgnoreCase("EUR") ||
               currency.equalsIgnoreCase("RSD") ||
               currency.equalsIgnoreCase("CHF") ||
               currency.equalsIgnoreCase("GBP");
    }
}
