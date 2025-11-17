package com.example.cryptoexchange;

import java.util.List;

public interface CryptoExchangeService {
    List<CryptoExchangeModel> getAllExchanges();
    CryptoExchangeModel getExchange(String fromCurrency, String toCurrency);
    CryptoExchangeModel createExchange(CryptoExchangeModel model);
    CryptoExchangeModel updateExchange(Long id, CryptoExchangeModel model);
    void deleteExchange(Long id);
}
