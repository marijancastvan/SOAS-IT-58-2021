package com.example.tradeservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dtos.CryptoWalletDto;
import api.proxies.CryptoWalletProxy;
import api.services.TradeService;
import api.proxies.CryptoExchangeProxy;
import api.proxies.BankAccountProxy;
import api.dtos.TradeRequestDto;
import api.dtos.TradeResultDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class TradeServiceImpl implements TradeService {

    private final CryptoWalletProxy walletProxy;
    private final CryptoExchangeProxy exchangeProxy;
    private final BankAccountProxy bankProxy;

    public TradeServiceImpl(CryptoWalletProxy walletProxy,
                            CryptoExchangeProxy exchangeProxy,
                            BankAccountProxy bankProxy) {
        this.walletProxy = walletProxy;
        this.exchangeProxy = exchangeProxy;
        this.bankProxy = bankProxy;
    }

    @Override
    public TradeResultDto executeTrade(TradeRequestDto dto) {
        // Dohvati wallet i banku
       // CryptoWalletDto wallet = walletProxy.getWalletByEmail(dto.email);
    	CryptoWalletDto wallet = walletProxy.getWalletByEmail(
    	        dto.email,
    	        "USER",
    	        dto.email
    	);

        //var bankAccount = bankProxy.getByEmail(dto.email);
    	var bankAccount = bankProxy.getByEmail(
    	        dto.email,
    	        "USER",
    	        dto.email
    	);


        if (wallet == null) throw new RuntimeException("Wallet not found for email: " + dto.email);
        if (bankAccount == null) throw new RuntimeException("BankAccount not found for email: " + dto.email);

        // Provera role korisnika
        String role = wallet.role;
        if ("OWNER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: only USER role allowed");
        }

        boolean fromIsFiat = isFiat(dto.fromCurrency);
        boolean toIsFiat = isFiat(dto.toCurrency);

        BigDecimal convertedAmount;

        if (fromIsFiat && !toIsFiat) {
            // Fiat -> Crypto
            BigDecimal fromBalance = bankAccount.getAmount(dto.fromCurrency);
            if (fromBalance.compareTo(dto.amount) < 0) throw new RuntimeException("Insufficient fiat funds");

            // Intermediate valuta: USD ili EUR
            String intermediate = (dto.fromCurrency.equals("USD") || dto.fromCurrency.equals("EUR")) ? dto.fromCurrency : "USD";

            BigDecimal rateToIntermediate = exchangeProxy.getRate(dto.fromCurrency, intermediate);
            BigDecimal intermediateAmount = dto.amount.multiply(rateToIntermediate);

            BigDecimal cryptoRate = exchangeProxy.getRate(intermediate, dto.toCurrency);
            convertedAmount = intermediateAmount.multiply(cryptoRate);

            // Update stanja
            bankAccount.updateAmount(dto.fromCurrency, fromBalance.subtract(dto.amount));
            //bankProxy.updateWallet(dto.email, bankAccount);
            bankProxy.updateWallet(
                    dto.email,
                    bankAccount,
                    "USER",
                    dto.email
            );


            wallet.updateAmount(dto.toCurrency, wallet.getAmount(dto.toCurrency).add(convertedAmount));
            //walletProxy.updateWallet(dto.email, wallet);
            walletProxy.updateWallet(
                    dto.email,
                    wallet,
                    "USER",
                    dto.email
            );


        } else if (!fromIsFiat && toIsFiat) {
            // Crypto -> Fiat
            BigDecimal fromBalance = wallet.getAmount(dto.fromCurrency);
            if (fromBalance.compareTo(dto.amount) < 0) throw new RuntimeException("Insufficient crypto funds");

            // Intermediate valuta: USD ili EUR
            String intermediate = (dto.toCurrency.equals("USD") || dto.toCurrency.equals("EUR")) ? dto.toCurrency : "USD";

            BigDecimal cryptoRate = exchangeProxy.getRate(dto.fromCurrency, intermediate);
            BigDecimal intermediateAmount = dto.amount.multiply(cryptoRate);

            BigDecimal fiatRate = exchangeProxy.getRate(intermediate, dto.toCurrency);
            convertedAmount = intermediateAmount.multiply(fiatRate);

            // Update stanja
            wallet.updateAmount(dto.fromCurrency, fromBalance.subtract(dto.amount));
            //walletProxy.updateWallet(dto.email, wallet);
            walletProxy.updateWallet(
                    dto.email,
                    wallet,
                    "USER",
                    dto.email
            );


            bankAccount.updateAmount(dto.toCurrency, bankAccount.getAmount(dto.toCurrency).add(convertedAmount));
            //bankProxy.updateWallet(dto.email, bankAccount);
            bankProxy.updateWallet(
                    dto.email,
                    bankAccount,
                    "USER",
                    dto.email
            );


        } else {
            throw new RuntimeException("Invalid currency conversion: must be fiat->crypto or crypto->fiat");
        }

        String message = String.format("Trade executed: %s %s -> %s %s",
                dto.amount, dto.fromCurrency, convertedAmount, dto.toCurrency);

        Map<String, BigDecimal> walletState = new HashMap<>();
        walletState.put(dto.toCurrency, wallet.getAmount(dto.toCurrency));

        Map<String, BigDecimal> bankState = new HashMap<>();
        bankState.put(dto.toCurrency, bankAccount.getAmount(dto.toCurrency));

        return new TradeResultDto(message, walletState, bankState);
    }

    private boolean isFiat(String currency) {
        return currency.equalsIgnoreCase("USD") ||
               currency.equalsIgnoreCase("EUR") ||
               currency.equalsIgnoreCase("RSD") ||
               currency.equalsIgnoreCase("CHF") ||
               currency.equalsIgnoreCase("GBP");
    }
}
