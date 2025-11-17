package com.example.currency_exchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Util.exceptions.CurrencyDoesntExistException;
import com.example.Util.exceptions.NoDataFoundException;

import api.dtos.CurrencyExchangeDto;
import api.services.CurrencyExchangeService;

@Service
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    @Autowired
    private CurrencyExchangeRepository repo;

    @Override
    public CurrencyExchangeDto getCurrencyExchange(String from, String to) {
        // Dohvati sve validne valute
        List<String> validCurrencies = repo.findAllDistinctCurrencies();

        // Proveri da li su unete valute validne
        if (!isValidCurrency(from, validCurrencies)) {
            throw new CurrencyDoesntExistException(
                String.format("Currency %s does not exist in the database", from),
                validCurrencies
            );
        }
        if (!isValidCurrency(to, validCurrencies)) {
            throw new CurrencyDoesntExistException(
                String.format("Currency %s does not exist in the database", to),
                validCurrencies
            );
        }

        // Pronađi kurs u bazi
        CurrencyExchangeModel dbResponse = repo.findByFromAndTo(from, to);
        if (dbResponse == null) {
            throw new NoDataFoundException(
                String.format("Requested exchange rate [%s to %s] does not exist", from, to),
                validCurrencies
            );
        }

        // Kreiraj i vrati DTO
        return new CurrencyExchangeDto(
                dbResponse.getFrom(),
                dbResponse.getTo(),
                dbResponse.getExchangeRate()
        );
    }

    private boolean isValidCurrency(String currency, List<String> validCurrencies) {
        return validCurrencies.stream()
                              .anyMatch(s -> s.equalsIgnoreCase(currency));
    }
}
