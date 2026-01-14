package api.proxies;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import api.dtos.UserDto;

@FeignClient("users-service")
	public interface UsersServiceProxy {
	
		@GetMapping("/users/email")
		UserDto getUserByEmailFeign(@RequestParam(value="email") String email);
		
	}
		
