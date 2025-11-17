package com.example.cryptoconversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = {"com.example.cryptoconversion", "api.proxies"})
public class CryptoConversionApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoConversionApplication.class, args);
    }
}
