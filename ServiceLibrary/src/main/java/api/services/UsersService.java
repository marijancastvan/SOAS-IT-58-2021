package api.services;

import java.util.List;

import org.springframework.http.ResponseEntity;

import api.dtos.UserDto;

public interface UsersService {
    List<UserDto> getUsers();
    UserDto getUserByEmail(String email);
    ResponseEntity<?> createAdmin(UserDto dto);
    ResponseEntity<?> createUser(UserDto dto);
    ResponseEntity<?> createOwner(UserDto dto);
    ResponseEntity<?> updateUser(UserDto dto);
    ResponseEntity<?> deleteUser(String email);
}
