package com.example.cryptoconversion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.Util.exceptions", "com.example.cryptoconversion","api.services"})
@EnableFeignClients(basePackages = {"api.proxies"})
public class CryptoConversionApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoConversionApplication.class, args);
    }
}
