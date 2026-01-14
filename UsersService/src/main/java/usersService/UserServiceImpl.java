package usersService;

import java.util.ArrayList;
import java.util.List;

import org.apache.catalina.User;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.BankAccountDto;
import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.BankAccountProxy;
import api.proxies.CryptoWalletProxy;
import api.services.UsersService;

@RestController
public class UserServiceImpl implements UsersService{

	@Autowired
	private UserRepository repo;
	
	@Autowired
	private BankAccountProxy bankAccountProxy;
	
	@Autowired
	private CryptoWalletProxy cryptoWalletProxy;
	
	@Override
	public List<UserDto> getUsers() {
		List<UserModel> models = repo.findAll();
		List<UserDto> dtos = new ArrayList<UserDto>();
		for(UserModel model : models) {
			dtos.add(convertModelToDto(model));
		}
		return dtos;
		
	}
	
	// Pravimo novog korisnika
		@Override
		public ResponseEntity<?> createUser(UserDto dto, @RequestHeader("Authorization") String authorizationHeader){
			String role = extractRoleFromAuthorizationHeader(authorizationHeader);
			
			UserModel user = convertDtoToModel(dto);
			
			try {
				
				switch(role) {
				
				case "ADMIN":
					// admin moze da dodaje i azurira samo usere
					if(!"USER".equals(dto.getRole())) {
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
								.body("Only Admin can create useers with role: 'USER'");
					}
					// proveravamo da li user vec postoji
					if(repo.findByEmail(dto.getEmail()) != null) {
						String errorMessage = "User with username " + user.getEmail() + " already exists";
						return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
					}
					
					//  cuvamo usera u nasoj bazi
					UserModel createdUser = repo.save(user);
					
					//	kada se kreira korisnik automatski se kreira i bankovni racun
					BankAccountDto bankAccountDto = new BankAccountDto();
					bankAccountDto.setEmail(dto.getEmail());
					ResponseEntity<?> bankAccountResponse = bankAccountProxy.createBankAccount(bankAccountDto);
					if (!bankAccountResponse.getStatusCode().is2xxSuccessful()) {
						repo.delete(createdUser); // brisemo usera ako pravljenje bank account-a failuje
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
								.body("Failed to create bank account for user: " + createdUser);
						
					}
					
					//	automatski kreiramo i crypto wallet pri kreiranju korisnika
					CryptoWalletDto cryptoWalletDto = new CryptoWalletDto();
	                cryptoWalletDto.setEmail(dto.getEmail());
	                ResponseEntity<?> cryptoWalletResponse = cryptoWalletProxy.createWallet(cryptoWalletDto);
	                if (!cryptoWalletResponse.getStatusCode().is2xxSuccessful()) {
	                    repo.delete(createdUser); // brisemo usera ako pravljenje crypto walleta failuje
	                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create crypto wallet for user: " + createdUser);
	                    
	                }
	                
	                return ResponseEntity.ok(createdUser);
					
				
				case "OWNER":
					//owner ima mogucnost dodavanja, modifikacije i brisanja svih korisnika
					//ako je neko logovan kao owner, znaci da ne mozemo da pravimo novog ownera
					if("OWNER".equals(user.getRole())) {
						return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with role 'OWNER' already exists");

					}
					
					UserModel checkUser = repo.findByEmail(dto.getEmail());
					
					//provera da je user vec kreiran
					if(checkUser != null) {
						
						if (checkUser.getRole().equals("USER")) {
							String errorMessage = "User with username " + user.getEmail() + " already exists";
							return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
						}
						if (checkUser.getRole().equals("ADMIN")) {
							String errorMessage = "Admin with username " + user.getEmail() + " already exists";
							return ResponseEntity.status(HttpStatus.CONFLICT).body(errorMessage);
							
						}
					}
					
					
					// saveujemo usera u nasoj bazi
					createdUser = repo.save(user);
					
					//automatski se kreira i racun ako je user-a kreirao owner
					if (createdUser.getRole().equals("USER")) {
						BankAccountDto bankAccountDtoUser = new BankAccountDto();
						bankAccountDtoUser.setEmail(dto.getEmail());
						ResponseEntity<?> bankAccountResponseUser = bankAccountProxy.createBankAccount(bankAccountDtoUser);
						if (!bankAccountResponseUser.getStatusCode().is2xxSuccessful()) {
							repo.delete(createdUser); // brisemo usera ako pravljenje bank account-a failuje
							return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
									.body("Failed to create bank account for user: " + createdUser);
							
						}
						
						 //Automatki se kreira i crypto novcanik pri kreiranju korisnika ako je user-a kreirao owner
						CryptoWalletDto cryptoWalletDtoUser = new CryptoWalletDto();
		                cryptoWalletDtoUser.setEmail(dto.getEmail());
		                ResponseEntity<?> cryptoWalletResponseUser = cryptoWalletProxy.createWallet(cryptoWalletDtoUser);
		                if (!cryptoWalletResponseUser.getStatusCode().is2xxSuccessful()) {
		                    repo.delete(createdUser); // ponistavamo kreiranje usera ako failuje pravljenje crypto walleta
		                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create crypto wallet for user: " + createdUser);
		                    
		                }
						
					}
					
					return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
					
				default:
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role or unauthorized access");

				}

			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error while creating user: " +e.getMessage());
			}
		}
		
		//Brisanje user-a
		@Override
		public ResponseEntity<?> deleteUser(@PathVariable int id){
			UserModel user = repo.findById(id).orElse(null);
			
			if(user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found");
			}
			
			repo.deleteById(id);
			
			if(user.getRole().equals("USER")) {
				bankAccountProxy.deleteBankAccount(user.getEmail(), "true");
				cryptoWalletProxy.deleteWallet(user.getEmail(), "true");
			}
			
			return ResponseEntity.ok("User with ID: " + id + " has been deleted");
		}
		
		// Update user
		@Override
		public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody UserDto dto,
				@RequestHeader("Authorization") String authorizationHeader){
			String role = extractRoleFromAuthorizationHeader(authorizationHeader);
			
			UserModel user = repo.findById(id).orElse(null);
			
			if (user == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with ID " + id + " not found");
			
			}
			String userEmail = user.getEmail();
			
			// Provera da li novi email već postoji kod drugog korisnika
			UserModel userWithNewEmail = repo.findByEmail(dto.getEmail());
			if (userWithNewEmail != null && userWithNewEmail.getId() != id) {
				return ResponseEntity.status(HttpStatus.CONFLICT)
						.body("Email " + dto.getEmail() + " is already taken by another user");
			}
			
			if ("ADMIN".equals(role)) {
				if ("USER".equals(user.getRole())) {
					if ("OWNER".equals(dto.getRole()) && repo.existsByRole("OWNER")) {
						return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with role 'OWNER' already exists");
					}
					//ako se menja role usera i vise nije user, brisemo racun i crypto wallet 
					if(!dto.getRole().equals("USER")) {
						bankAccountProxy.deleteBankAccount(user.getEmail(),"true" );
						cryptoWalletProxy.deleteWallet(user.getEmail(), "true");
					} else {
						bankAccountProxy.updateEmail(user.getEmail(), dto.getEmail());
						cryptoWalletProxy.updateEmail(user.getEmail(), dto.getEmail());
					}
					user.setEmail(dto.getEmail());
					user.setPassword(dto.getPassword());
					user.setRole(dto.getRole());
					repo.save(user);

					return ResponseEntity.ok(convertModelToDto(user));
				} else {
					return ResponseEntity.status(HttpStatus.FORBIDDEN)
							.body("Admin can only update users with role 'USER'");
				}
			} else if ("OWNER".equals(role)) {

				if (user.getRole().equals("OWNER") && dto.getRole().equals("OWNER")) {
					// mogu da se menjaju email i password
					user.setEmail(dto.getEmail());
					user.setPassword(dto.getPassword());
					repo.save(user);
					return ResponseEntity.ok(convertModelToDto(user));
				}
				// ako neki drugi korisnik postaje owner od strane ownera
				else if ("OWNER".equals(dto.getRole()) && !user.getRole().equals("OWNER") && repo.existsByRole("OWNER")) {
					return ResponseEntity.status(HttpStatus.CONFLICT).body("A user with role 'OWNER' already exists");
					
				} else {
					if ("USER".equals(user.getRole())) {
						//ako rolu usera menja u nesto drugo
						
						if(!dto.getRole().equals("USER")) {
							bankAccountProxy.deleteBankAccount(userEmail,"true" );
							cryptoWalletProxy.deleteWallet(userEmail, "true");
						} else {
							bankAccountProxy.updateEmail(userEmail, dto.getEmail());
							cryptoWalletProxy.updateEmail(userEmail, dto.getEmail());
						}
					}
					if ("ADMIN".equals(user.getRole())) {
						//ako rolu admina menja u usera
						if(dto.getRole().equals("USER")) {
							//pravimo bank racun i crypto wallet
							BankAccountDto bankAccountDtoUser = new BankAccountDto();
							bankAccountDtoUser.setEmail(dto.getEmail());
							ResponseEntity<?> bankAccountResponseUser = bankAccountProxy.createBankAccount(bankAccountDtoUser);
							if (!bankAccountResponseUser.getStatusCode().is2xxSuccessful()) {
								return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
										.body("Failed to create bank account for user");
							}
							 //Automatsko kreiranje crypto novcanika pri kreiranju korisnika 
							CryptoWalletDto cryptoWalletDtoUser = new CryptoWalletDto();
			                cryptoWalletDtoUser.setEmail(dto.getEmail());
			                ResponseEntity<?> cryptoWalletResponseUser = cryptoWalletProxy.createWallet(cryptoWalletDtoUser);
			                if (!cryptoWalletResponseUser.getStatusCode().is2xxSuccessful()) {
			                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create crypto wallet for user");
			                }
						}
					}
					user.setEmail(dto.getEmail());
					user.setPassword(dto.getPassword());
					user.setRole(dto.getRole());
					repo.save(user);

					return ResponseEntity.ok(convertModelToDto(user));
				}
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized role");
			}
		}


	@Override
	public UserDto getUserByEmail(String email) {
		if(repo.findByEmail(email) != null) {
			return convertModelToDto(repo.findByEmail(email));
		} else {
			return null;
		}
	}

	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
			
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email already exists");
		}
	}
	
	public String extractRoleFromAuthorizationHeader(String authorizationHeader)
	{
		try {
			String encodedCredentials = authorizationHeader.replaceFirst("Basic ", "");
			//string dekodiramo nazad u prvobitni oblik (username:password	"user@example.com : password")								
			byte[] decodedBytes = Base64.decode(encodedCredentials.getBytes());
			
			String decodedCredentials = new String(decodedBytes);
			String[] credentials = decodedCredentials.split(":");
			// email je prvi unet kao username korisnika
			String email = credentials[0];
			UserModel user = repo.findByEmail(email);
			
			if(user != null) {
				return user.getRole();
			} else {
				System.out.println("User with email: " + email + " not found");
				return null;
			}
			
		}
		catch(Exception e){
			System.out.println("Error extracting role: " + e.getMessage());
			return null;
		}
			
		
	}

	/*
	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("USER");
			UserModel model = convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
			
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exists");
		}
	}*/
	
	/*
	@Override
	public ResponseEntity<?> updateUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
			return ResponseEntity.status(HttpStatus.OK).body(dto);
			
		} else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email does not exists");
		}
	}*/
	
	
	public UserDto convertModelToDto(UserModel model) {
		return new UserDto(model.getEmail(), model.getPassword(), model.getRole());
	}
	
	public UserModel convertDtoToModel(UserDto dto) {
		return new UserModel(dto.getEmail(), dto.getPassword(), dto.getRole());
	}

}