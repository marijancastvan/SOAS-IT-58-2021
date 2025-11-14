package com.example.currency_exchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.Util.exceptions.CurrencyDoesntExistException;
import com.example.Util.exceptions.NoDataFoundException;

import api.dtos.CurrencyExchangeDto;
import api.services.CurrencyExchangeService;

@RestController
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {

    @Autowired
    private CurrencyExchangeRepository repo;

    @Autowired
    private Environment environment;

    @Override
    public ResponseEntity<?> getCurrencyExchange(String from, String to) {
        // Dohvati sve validne valute iz baze samo jednom
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

        // Napravi DTO i dodaj port
        CurrencyExchangeDto dto = new CurrencyExchangeDto(
                dbResponse.getFrom(),
                dbResponse.getTo(),
                dbResponse.getExchangeRate()
        );
        dto.setPort(environment.getProperty("local.server.port"));

        return ResponseEntity.ok(dto);
    }

    // Pomoćna metoda za proveru validnosti valute
    private boolean isValidCurrency(String currency, List<String> validCurrencies) {
        return validCurrencies.stream()
                              .anyMatch(s -> s.equalsIgnoreCase(currency));
    }
}
