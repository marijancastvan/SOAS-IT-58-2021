package com.example.cryptowallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.Util.exceptions", "com.example.cryptowallet","api.services"})
@EnableFeignClients(basePackages = {"api.proxies"})
public class CryptoWalletApplication {
    public static void main(String[] args) {
        SpringApplication.run(CryptoWalletApplication.class, args);
    }
}
