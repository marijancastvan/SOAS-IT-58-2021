package com.example.tradeservice;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import api.dtos.CryptoWalletDto;
import api.proxies.CryptoWalletProxy;
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
        CryptoWalletDto wallet = walletProxy.getWalletByEmail(dto.email);
        var bankAccount = bankProxy.getByEmail(dto.email);

        // Provera role korisnika
        String role = wallet.role; // ovo zavisi kako definišeš role u wallet dto
        if("OWNER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role)) {
            throw new RuntimeException("Access denied: only USER role allowed");
        }

        boolean fromIsFiat = isFiat(dto.fromCurrency);
        boolean toIsFiat = isFiat(dto.toCurrency);

        BigDecimal convertedAmount;

        if(fromIsFiat && !toIsFiat) {
            // Fiat -> Crypto
            BigDecimal fromBalance = bankProxy.getByEmail(dto.email).getAmount(dto.fromCurrency);
            if(fromBalance.compareTo(dto.amount) < 0) throw new RuntimeException("Insufficient fiat funds");

            // Prvo konvertuj u USD ili EUR ako nije
            String intermediate = (dto.fromCurrency.equals("USD") || dto.fromCurrency.equals("EUR")) ? dto.fromCurrency : "USD";

            BigDecimal rateToIntermediate = exchangeProxy.getRate(dto.fromCurrency, intermediate);
            BigDecimal intermediateAmount = dto.amount.multiply(rateToIntermediate);

            BigDecimal cryptoRate = exchangeProxy.getRate(intermediate, dto.toCurrency);
            convertedAmount = intermediateAmount.multiply(cryptoRate);

            // Update stanja
            bankProxy.updateWallet(dto.email, bankAccount);
            wallet.updateAmount(dto.toCurrency, wallet.getAmount(dto.toCurrency).add(convertedAmount));
            walletProxy.updateWallet(dto.email, wallet);

        } else if(!fromIsFiat && toIsFiat) {
            // Crypto -> Fiat
            BigDecimal fromBalance = wallet.getAmount(dto.fromCurrency);
            if(fromBalance.compareTo(dto.amount) < 0) throw new RuntimeException("Insufficient crypto funds");

            // Konvertuj u USD ili EUR ako nije
            String intermediate = (dto.toCurrency.equals("USD") || dto.toCurrency.equals("EUR")) ? dto.toCurrency : "USD";
            BigDecimal cryptoRate = exchangeProxy.getRate(dto.fromCurrency, intermediate);
            BigDecimal intermediateAmount = dto.amount.multiply(cryptoRate);

            BigDecimal fiatRate = exchangeProxy.getRate(intermediate, dto.toCurrency);
            convertedAmount = intermediateAmount.multiply(fiatRate);

            wallet.updateAmount(dto.fromCurrency, fromBalance.subtract(dto.amount));
            walletProxy.updateWallet(dto.email, wallet);

            bankAccount.updateAmount(dto.toCurrency, bankAccount.getAmount(dto.toCurrency).add(convertedAmount));
            bankProxy.updateWallet(dto.email, bankAccount);

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
