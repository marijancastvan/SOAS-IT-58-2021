package com.example.currency_exchange;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.Util.exceptions.CurrencyDoesntExistException;
import com.example.Util.exceptions.NoDataFoundException;

import api.dtos.CurrencyExchangeDto;
import api.services.CurrencyExchangeService;

@RestController
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService{

	@Autowired
	private CurrencyExchangeRepository repo;
	
	@Autowired
	private Environment environment;
	
	@Override
	public ResponseEntity<?> getCurrencyExchange(String from, String to) {
		String missingCurrency = null;
		List<String> validCurrencies = repo.findAllDistinctCurrencis();

		if(!isValidCurrency(from)) missingCurrency = from;

		else if(!isValidCurrency(to)) missingCurrency = to;

		if(missingCurrency != null) throw new 
		CurrencyDoesntExistException(String.format("Currency %s does not exist in the database", missingCurrency),
				validCurrencies);

		CurrencyExchangeModel dbResponse  = repo.findByFromAndTo(from, to);

		if(dbResponse == null) {
			throw new NoDataFoundException(String.format("Requested exchange rate [%s to %s] does not exist", from,to),
					validCurrencies);
		}

		CurrencyExchangeDto dto = new CurrencyExchangeDto(dbResponse.getFrom(), dbResponse.getTo(), dbResponse.getExchangeRate());
		dto.setPort(environment.getProperty("local.server.port"));
		return ResponseEntity.ok(dto);
	}

	public boolean isValidCurrency(String currency) {
		List<String> currencies = repo.findAllDistinctCurrencis();
		for(String s: currencies) {
			if(s.equalsIgnoreCase(currency))
				return true;
		}
		return false;
	}

}