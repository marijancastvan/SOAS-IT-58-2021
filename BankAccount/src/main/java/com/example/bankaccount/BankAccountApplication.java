package com.example.bankaccount;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.example.Util.exceptions", "com.example.bankaccount", "api.services"})
@EnableFeignClients(basePackages = "api.proxies")
public class BankAccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankAccountApplication.class, args);
    }
}
