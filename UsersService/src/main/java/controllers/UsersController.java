package controllers;

import api.dtos.UserDto;
import api.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsersController {

    private final UsersService service;

    public UsersController(UsersService service) {
        this.service = service;
    }

    @GetMapping("/users")
    public List<UserDto> getUsers() {
        return service.getUsers();
    }

    @GetMapping("/users/email")
    public UserDto getUserByEmail(@RequestParam String email) {
        return service.getUserByEmail(email);
    }

    @PostMapping("/users/newAdmin")
    public ResponseEntity<?> createAdmin(@RequestBody UserDto dto) {
        return service.createAdmin(dto);
    }

    @PostMapping("/users/newUser")
    public ResponseEntity<?> createUser(@RequestBody UserDto dto) {
        return service.createUser(dto);
    }

    @PostMapping("/users/newOwner")
    public ResponseEntity<?> createOwner(@RequestBody UserDto dto) {
        return service.createOwner(dto);
    }

    @PutMapping("/users")
    public ResponseEntity<?> updateUser(@RequestBody UserDto dto) {
        return service.updateUser(dto);
    }
    
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestParam String email) {
        return service.deleteUser(email);
    }
    
    @DeleteMapping("/users/{email}")
    public ResponseEntity<?> deleteUserEndpoint(@PathVariable String email) {
        return deleteUser(email);
    }


}
