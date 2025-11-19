package com.example.bankaccount;

import api.dtos.BankAccountDto;
import api.proxies.UsersServiceProxy;
import api.services.BankAccountService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BankAccountServiceImpl implements BankAccountService {

    private final BankAccountRepository repo;
    private final UsersServiceProxy usersProxy;

    public BankAccountServiceImpl(BankAccountRepository repo, UsersServiceProxy usersProxy) {
        this.repo = repo;
        this.usersProxy = usersProxy;
    }

    @Override
    public List<BankAccountDto> getAll() {
        return repo.findAll().stream()
                   .map(this::toDto)
                   .toList();
    }

    @Override
    public BankAccountDto getByEmail(String email) {
        BankAccountModel acc = repo.findByEmail(email)
                                   .orElseThrow(() -> new RuntimeException("Account not found"));
        return toDto(acc);
    }

    @Override
    public BankAccountDto createForUser(String email) {
        api.dtos.UserDto user = usersProxy.getUserByEmail(email);
        if (user == null) throw new RuntimeException("Related user not found");
        if (!"USER".equalsIgnoreCase(user.getRole())) throw new RuntimeException("Bank account allowed only for role USER");

        if (repo.existsByEmail(email)) throw new RuntimeException("Account already exists");

        BankAccountModel account = new BankAccountModel(email);
        return toDto(repo.save(account));
    }

    @Override
    public BankAccountDto update(String email, BankAccountDto dto) {
        BankAccountModel acc = repo.findByEmail(email)
                                   .orElseThrow(() -> new RuntimeException("Account not found"));

        if (dto.usd != null) acc.setUsd(dto.usd);
        if (dto.eur != null) acc.setEur(dto.eur);
        if (dto.gbp != null) acc.setGbp(dto.gbp);
        if (dto.chf != null) acc.setChf(dto.chf);
        if (dto.rsd != null) acc.setRsd(dto.rsd);

        return toDto(repo.save(acc));
    }

    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }

    private BankAccountDto toDto(BankAccountModel model) {
        BankAccountDto dto = new BankAccountDto(model.getEmail());
        dto.setAmount("USD", model.getUsd());
        dto.setAmount("EUR", model.getEur());
        dto.setAmount("GBP", model.getGbp());
        dto.setAmount("CHF", model.getChf());
        dto.setAmount("RSD", model.getRsd());
        return dto;
    }

}
