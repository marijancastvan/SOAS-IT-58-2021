package api.services;

import api.dtos.CurrencyExchangeDto;

public interface CurrencyExchangeService {
    CurrencyExchangeDto getCurrencyExchange(String from, String to);
}
