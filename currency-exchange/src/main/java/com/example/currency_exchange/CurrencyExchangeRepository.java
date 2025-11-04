package com.example.currency_exchange;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchangeModel, Integer>{
	
	CurrencyExchangeModel findByFromAndTo(String from, String to);

}