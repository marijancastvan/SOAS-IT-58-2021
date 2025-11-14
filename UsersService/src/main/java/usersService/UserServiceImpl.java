package usersService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import api.dtos.UserDto;
import api.services.UsersService;

@RestController
@RequestMapping("/api")  
public class UserServiceImpl implements UsersService {

	@Autowired
	private UserRepository repo;
	
	@PostMapping("/owner")
	public ResponseEntity<?> createOwnerEndpoint(@RequestBody UserDto dto) {
	    return createOwner(dto);
	}

	
	private void checkOwnerAccess(UserDto currentUser) {
	    if (!"OWNER".equalsIgnoreCase(currentUser.getRole())) {
	        throw new RuntimeException("Access denied: Only OWNER can perform this action");
	    }
	}

	
	@Override
	public List<UserDto> getUsers() {
		List<UserModel> models = repo.findAll();
		List<UserDto> dtos = new ArrayList<UserDto>();
		for(UserModel model: models) {
			dtos.add(convertModelToDto(model));
		}
		return dtos;
	}

	@Override
	public UserDto getUserByEmail(String email) {
		return convertModelToDto(repo.findByEmail(email));
	}

	@Override
	public ResponseEntity<?> createAdmin(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("ADMIN");
			UserModel model= convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Admin with passed email already exist");
			
		}
	}
	
	@Override
	public ResponseEntity<?> createOwner(UserDto dto) {
	    // Proveri da li već postoji OWNER
	    boolean ownerExists = repo.findAll().stream()
	            .anyMatch(u -> u.getRole().equalsIgnoreCase("OWNER"));

	    if (ownerExists) {
	        return ResponseEntity.status(HttpStatus.CONFLICT)
	                .body("An OWNER user already exists");
	    }

	    dto.setRole("OWNER");
	    UserModel owner = convertDtoToModel(dto);
	    repo.save(owner);
	    return ResponseEntity.status(HttpStatus.CREATED).body(owner);
	}


	@Override
	public ResponseEntity<?> createUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) == null) {
			dto.setRole("USER");
			UserModel model= convertDtoToModel(dto);
			return ResponseEntity.status(HttpStatus.CREATED).body(repo.save(model));
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exist");
			
		}
	}

	@Override
	public ResponseEntity<?> updateUser(UserDto dto) {
		if(repo.findByEmail(dto.getEmail()) != null) {
			
			repo.updateUser(dto.getEmail(), dto.getPassword(), dto.getRole());
			return ResponseEntity.status(HttpStatus.OK).body(dto);
		}else {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("User with passed email already exist");
			
		}
	}
	
	public UserDto convertModelToDto(UserModel model)
	{
		return new UserDto(model.getEmail(),model.getPassword(),model.getRole());
		
	}
	public UserModel convertDtoToModel(UserDto dto)
	{
		return new UserModel(dto.getEmail(),dto.getPassword(),dto.getRole());
		
	}
	
}