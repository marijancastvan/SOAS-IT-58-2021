package api.services;

import java.util.List;
import api.dtos.CryptoExchangeDto;

public interface CryptoExchangeService {
    List<CryptoExchangeDto> getAllExchanges();
    CryptoExchangeDto getExchange(String fromCurrency, String toCurrency);
    CryptoExchangeDto createExchange(CryptoExchangeDto dto);
    CryptoExchangeDto updateExchange(Long id, CryptoExchangeDto dto);
    void deleteExchange(Long id);
}
