package com.example.cryptoexchange;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import api.dtos.CryptoExchangeDto;
import api.services.CryptoExchangeService;

@Service
@Transactional
public class CryptoExchangeServiceImpl implements CryptoExchangeService {

    private final CryptoExchangeRepository repo;

    public CryptoExchangeServiceImpl(CryptoExchangeRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<CryptoExchangeDto> getAllExchanges() {
        return repo.findAll().stream()
                   .map(this::toDto)
                   .collect(Collectors.toList());
    }

    @Override
    public CryptoExchangeDto getExchange(String fromCurrency, String toCurrency) {
        return repo.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                   .map(this::toDto)
                   .orElseThrow(() -> new RuntimeException("Exchange not found"));
    }

    @Override
    public CryptoExchangeDto createExchange(CryptoExchangeDto dto) {
        CryptoExchangeModel model = new CryptoExchangeModel(dto.fromCurrency, dto.toCurrency, dto.rate);
        return toDto(repo.save(model));
    }

    @Override
    public CryptoExchangeDto updateExchange(Long id, CryptoExchangeDto dto) {
        CryptoExchangeModel existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        existing.setFromCurrency(dto.fromCurrency);
        existing.setToCurrency(dto.toCurrency);
        existing.setRate(dto.rate);
        return toDto(repo.save(existing));
    }

    @Override
    public void deleteExchange(Long id) {
        repo.deleteById(id);
    }

    private CryptoExchangeDto toDto(CryptoExchangeModel model) {
        return new CryptoExchangeDto(
            model.getId(),          
            model.getFromCurrency(),
            model.getToCurrency(),
            model.getRate()
        );
    }

}
