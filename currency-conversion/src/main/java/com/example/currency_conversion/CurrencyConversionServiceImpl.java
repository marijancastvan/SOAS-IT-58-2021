package com.example.currency_conversion;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.Util.exceptions.InvalidQuantityException;

import api.dtos.BankAccountDto;
import api.dtos.CurrencyConversionDto;
import api.dtos.CurrencyExchangeDto;
import api.dtos.FiatBalanceDto;
import api.dtos.UserDto;
import api.proxies.BankAccountProxy;
import api.proxies.CurrencyExchangeProxy;
import api.proxies.UsersServiceProxy;
import api.services.CurrencyConversionService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;


@RestController
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

	
	private RestTemplate template = new RestTemplate();
	
	@Autowired
	private CurrencyExchangeProxy exchangeProxy;
	
	@Autowired
	private UsersServiceProxy usersProxy;
	
	@Autowired
	private BankAccountProxy bankAccountProxy;
	
	Retry retry;
	CurrencyExchangeDto response;
	
	public CurrencyConversionServiceImpl (RetryRegistry registry) {
		retry = registry.retry("default");
	}
	
	@Override
	@CircuitBreaker(name = "cb", fallbackMethod = "fallback")
	public ResponseEntity<?> getConversionFeign(String from, String to, BigDecimal quantity,  @RequestHeader("Authorization") String authorizationHeader){
		 try {
		        UserDto user = usersProxy.getUserByEmailFeign(getEmailFromAuthHeader(authorizationHeader));

		        BankAccountDto bankAccount = bankAccountProxy.getBankAccountForUser(authorizationHeader);

		        if (bankAccount == null) {
		           return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found for user.");
		        }
		        
		        BigDecimal accountCurrencyAmountTo = bankAccountProxy.getUserCurrencyAmount(user.getEmail(), to);
		        if(accountCurrencyAmountTo == null) {
		        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currency " + to + " is not valid.");
		        }

		        BigDecimal accountCurrencyAmountFrom = bankAccountProxy.getUserCurrencyAmount(user.getEmail(), from);
		        if(accountCurrencyAmountFrom == null) {
		        	return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Currency " + from + " is not valid.");
		        }
		        
		        if (accountCurrencyAmountFrom.compareTo(quantity) < 0) {
		        	throw new InvalidQuantityException(String.format("Quantity of " + quantity + " is too large, User bank account currency balance: (" + from + ") " + accountCurrencyAmountFrom));
		        }

		        retry.executeSupplier(()-> response = exchangeProxy.getExchangeFeign(from, to).getBody());
		        
		        if (response == null) {
		            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exchange service response is null.");
		        }

		        BigDecimal exchangeValue = response.getExchangeRate();
		        BigDecimal totalExchanged = exchangeValue.multiply(quantity);
		        
		        FiatBalanceDto fiatFromDto = new FiatBalanceDto(from, accountCurrencyAmountFrom.subtract(quantity));
				FiatBalanceDto fiatToDto =  new FiatBalanceDto(to, accountCurrencyAmountTo.add(totalExchanged));
				
				ArrayList<FiatBalanceDto> newList = new ArrayList<FiatBalanceDto>();
				
				newList.add(fiatFromDto);
				newList.add(fiatToDto);
				
				
				BankAccountDto updateAccount = new BankAccountDto(
						user.getEmail(),
						newList
				);
						
				
				ResponseEntity<?> updatedBalances = bankAccountProxy.updateBankAccount(user.getEmail(), updateAccount);
				
				
				CurrencyConversionDto finalResponse = new CurrencyConversionDto(response, quantity);
				finalResponse.setFeign(true);
				
				BankAccountDto updatedBankAccount = bankAccountProxy.getBankAccountForUser(authorizationHeader);
				String message = "Conversion was successfull! " + from + ":" + quantity + " is exchanged for " + to + ":" + totalExchanged;
						
				
				Map<String, Object> responseBody = new HashMap<>();
				responseBody.put("bankAccount", updatedBankAccount);
				responseBody.put("message", message);

				// uzimamo kao response entity
				return ResponseEntity.ok(responseBody);
				
		 } catch (Exception ex) {
		        ex.printStackTrace();
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
		                .body("An unexpected error occurred: " + ex.getMessage());
		    }
		}
	
	public String getEmailFromAuthHeader(String authorizationHeader) {
		try {
			String encodedCredentials = authorizationHeader.replace("Basic ", "");
			byte[] decodedBytes = Base64.decode(encodedCredentials.getBytes());
			String decodedCredentials = new String(decodedBytes);
			String[] credentials = decodedCredentials.split(":");
			String email = credentials[0];
			return email;
		} catch(Exception e) {
			System.out.println("Error while extracting email " + e.getMessage());
			return null;
		}
	}
	
	public ResponseEntity<?> fallback(CallNotPermittedException ex){
		return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Currency conversion service"
				+ " is currently unavailable, Circuit breaker is in OPEN state");
	}
	
	@Override
	public ResponseEntity<?> getConversion(String from, String to, BigDecimal quantity) {
		
		if(quantity.compareTo(BigDecimal.valueOf(300.0)) == 1) {
			throw new InvalidQuantityException(String.format("Quantity of %s"
					+ " is too large", quantity));
		}
		
		String endPoint = "http://localhost:8000/currency-exchange?from=" + from +
				"&to=" + to;
		ResponseEntity<CurrencyExchangeDto> response;
		response = template.getForEntity(endPoint, CurrencyExchangeDto.class);
		
		return ResponseEntity.ok(new CurrencyConversionDto(response.getBody(), quantity));
	
	}

}