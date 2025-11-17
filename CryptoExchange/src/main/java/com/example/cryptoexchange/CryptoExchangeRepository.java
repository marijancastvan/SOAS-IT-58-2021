package com.example.cryptoexchange;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CryptoExchangeRepository extends JpaRepository<CryptoExchangeModel, Long> {
    Optional<CryptoExchangeModel> findByFromCurrencyAndToCurrency(String fromCurrency, String toCurrency);
}
