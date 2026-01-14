package com.example.bankaccount;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.FiatBalanceDto;
import api.proxies.UsersServiceProxy;
import api.services.BankAccountService;


@RestController
public class BankAccountServiceImpl implements BankAccountService {
	
	private final BankAccountRepository repo;
	private final UsersServiceProxy proxy;
	
	@Autowired
    public BankAccountServiceImpl(BankAccountRepository repository, UsersServiceProxy usersProxy) {
        this.repo = repository;
        this.proxy = usersProxy;
    }
	
	@Override
    public ResponseEntity<List<BankAccountDto>> getAllAccounts() {
        List<BankAccountModel> models = repo.findAll();
        List<BankAccountDto> dtos = models.stream().map(this::convertToDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

	@Override
	public ResponseEntity<?> getBankAccountByEmail(@PathVariable("email") String email) {
		BankAccountModel model = repo.findByEmail(email);
		if (model == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found for email: " + email);
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(convertToDto(model));
	}

	@Override
	public BankAccountDto getBankAccountForUser(@RequestHeader("Authorization") String authorizationHeader) {
		String email = this.getEmailFromAuthHeader(authorizationHeader);
		BankAccountModel model = repo.findByEmail(email);
		if (model == null) {
			return null;
		}
		
		return convertToDto(model);
	}

	
	//KREIRANJE BANKOVNOG RACUNA
    //ZAHVALJUJUCI PATH MATCHERS SAMO ADMIN MOZE DA PRISTUPI
	@Override
    public ResponseEntity<?> createBankAccount(@RequestBody BankAccountDto dto) {

        try {
                // Proveravam da li user postoji
                //UserDto userExists = usersProxy.getUserByEmailFeign(dto.getEmail());
                //if (userExists == null) {
                //    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with email " + dto.getEmail() + " does not exist.");
                //}

                // Proveravam da li user ima racun
                BankAccountModel existingAccount = repo.findByEmail(dto.getEmail());
                if (existingAccount != null) {
                    return ResponseEntity.badRequest().body("Bank account for user with email " + dto.getEmail() + " already exists");
                }

                // Kreiram novi racun sa 0 balansa za sve valute
                BankAccountModel bankAccount = new BankAccountModel(dto.getEmail());
                List<FiatBalanceModel> initialBalances = createInitialBalances(bankAccount);
                bankAccount.setFiatBalances(initialBalances);
                repo.save(bankAccount);

                return ResponseEntity.ok(Collections.singletonMap("message", "Bank account created successfully for user: " + dto.getEmail()));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Error creating bank account: " + e.getMessage()));
        }
    }
	
	
	//AZURIRANJE BANKOVNOG RACUNA
    //SAMO ADMIN
    @Override
	public ResponseEntity<?> updateBankAccount(@PathVariable String email, @RequestBody BankAccountDto dto) {

		    BankAccountModel bankAccount = repo.findByEmail(email);
		    if (bankAccount == null) {
		        return ResponseEntity.notFound().build();
		    }
		    
		    Map<String, FiatBalanceModel> existingBalances = bankAccount.getFiatBalances()
		            .stream()
		            .collect(Collectors.toMap(FiatBalanceModel::getCurrency, fb -> fb));

		    /*existingBalances = {
			"USD" -> FiatBalanceModel(currency="USD", balance=1000),
			"EUR" -> FiatBalanceModel(currency="EUR", balance=500)
			}*/
		    for (FiatBalanceDto dtoBalance : dto.getFiatBalances()) {
		        if (existingBalances.containsKey(dtoBalance.getCurrency())) {
		            existingBalances.get(dtoBalance.getCurrency()).setBalance(dtoBalance.getBalance());
		        } else {
		            FiatBalanceModel newBalance = new FiatBalanceModel(dtoBalance.getCurrency(), dtoBalance.getBalance());
		            newBalance.setBankAccount(bankAccount);
		            bankAccount.getFiatBalances().add(newBalance);
		        }
		    }

		    repo.save(bankAccount);

		    return ResponseEntity.ok("Bank account updated successfully");
	}
    

     // BRISANJE BANKOVNOG RACUNA
 	 // POZIVAM preko proxy-ja iz UserService
 	 @Override
 	 public ResponseEntity<?> deleteAccount(@PathVariable String email,
 			 @RequestHeader(value = "X-Internal-Call", required = false) String internalCall) {

 	     // Provera da li je zahtev internog servisa
 	     if (!"true".equals(internalCall)) {
 	         return ResponseEntity.status(HttpStatus.FORBIDDEN)
 	                              .body("This operation is allowed only via internal service calls.");
 	     }

 	     BankAccountModel bankAccount = repo.findByEmail(email);
 	     if (bankAccount != null) {
 	    	repo.delete(bankAccount);
 	         return ResponseEntity.ok("Bank account deleted successfully");
 	     } else {
 	         return ResponseEntity.status(HttpStatus.NOT_FOUND)
 	                              .body("Bank account not found for email: " + email);
 	     }
 	 }

 	@Override
	public ResponseEntity<?> updateEmail(@RequestParam String oldEmail, @RequestParam String newEmail) {
		  BankAccountModel bankAccount = repo.findByEmail(oldEmail);
		  if (bankAccount == null) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bank account not found for user with email: " + oldEmail);
		  }
		  
		  bankAccount.setEmail(newEmail);
		  
		  repo.save(bankAccount);

		  return ResponseEntity.ok(Collections.singletonMap("message", "Bank account email updated successfully"));
		  //return ResponseEntity.status(HttpStatus.OK).body("Bank account email updated successfully");
		
	}
 	

 	@Override
	public BigDecimal getUserCurrencyAmount(String email, String currencyFrom) {
		BankAccountModel userAccount = repo.findByEmail(email);
		if(userAccount == null) {
			return null;
		}
		
		List<FiatBalanceModel> balances = userAccount.getFiatBalances();
		
		for (FiatBalanceModel accountCurrency : balances) {
			if (accountCurrency.getCurrency().equals(currencyFrom)) 
			{
				return accountCurrency.getBalance();
			}
		}
		return null;
	}
	
 	
	private BankAccountDto convertToDto(BankAccountModel model) {
		    BankAccountDto dto = new BankAccountDto();
		    dto.setEmail(model.getEmail());

		    if (model.getFiatBalances() != null) {
		        List<FiatBalanceDto> fiatBalanceDtos = model.getFiatBalances().stream()
		                .map(fiatBalanceModel -> {
		                    FiatBalanceDto fiatBalanceDto = new FiatBalanceDto();
		                    fiatBalanceDto.setCurrency(fiatBalanceModel.getCurrency());
		                    fiatBalanceDto.setBalance(fiatBalanceModel.getBalance());
		                    return fiatBalanceDto;
		                })
		                .collect(Collectors.toList());
		        dto.setFiatBalances(fiatBalanceDtos);
		    }

		    return dto;
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
	 
	 private List<FiatBalanceModel> createInitialBalances(BankAccountModel bankAccount) {
		    List<FiatBalanceModel> balances = new ArrayList<>();
		    balances.add(new FiatBalanceModel("EUR", BigDecimal.ZERO));
		    balances.add(new FiatBalanceModel("USD", BigDecimal.ZERO));
		    balances.add(new FiatBalanceModel("GBP", BigDecimal.ZERO));
		    balances.add(new FiatBalanceModel("CHF", BigDecimal.ZERO));
		    balances.add(new FiatBalanceModel("CAD", BigDecimal.ZERO));
		    balances.add(new FiatBalanceModel("RSD", BigDecimal.ZERO));
		    for (FiatBalanceModel balance : balances) {
		        balance.setBankAccount(bankAccount);
		    }
		    return balances;
		}
	    

}