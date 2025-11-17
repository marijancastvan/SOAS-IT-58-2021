package com.example.cryptoexchange;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CryptoExchangeServiceImpl implements CryptoExchangeService {

    private final CryptoExchangeRepository repo;

    public CryptoExchangeServiceImpl(CryptoExchangeRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<CryptoExchangeModel> getAllExchanges() {
        return repo.findAll();
    }

    @Override
    public CryptoExchangeModel getExchange(String fromCurrency, String toCurrency) {
        return repo.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
    }

    @Override
    public CryptoExchangeModel createExchange(CryptoExchangeModel model) {
        return repo.save(model);
    }

    @Override
    public CryptoExchangeModel updateExchange(Long id, CryptoExchangeModel model) {
        CryptoExchangeModel existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        existing.setFromCurrency(model.getFromCurrency());
        existing.setToCurrency(model.getToCurrency());
        existing.setRate(model.getRate());
        return repo.save(existing);
    }

    @Override
    public void deleteExchange(Long id) {
        repo.deleteById(id);
    }
}
