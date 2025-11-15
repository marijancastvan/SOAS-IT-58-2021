package com.example.bankaccount;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.UserDto;

@FeignClient(name = "UsersService", url = "http://localhost:8770", path = "/api")
public interface UsersServiceProxy {
    @GetMapping("/users/email")
    UserDto getUserByEmail(@RequestParam String email);
}
