package com.example.bankaccount;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.math.BigDecimal;

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
    public List<BankAccountModel> getAll() {
        return repo.findAll();
    }

    @Override
    public BankAccountModel getByEmail(String email) {
        return repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    @Override
    public BankAccountModel createForUser(String email) {
        // validate user exists and role == USER
        api.dtos.UserDto user = usersProxy.getUserByEmail(email);
        if (user == null) throw new RuntimeException("Related user not found");
        if (!"USER".equalsIgnoreCase(user.getRole())) throw new RuntimeException("Bank account allowed only for role USER");

        if (repo.existsByEmail(email)) throw new RuntimeException("Account already exists");

        BankAccountModel account = new BankAccountModel(email);
        // amounts already default 0
        return repo.save(account);
    }

    @Override
    public BankAccountModel update(String email, BankAccountDto dto) {
        BankAccountModel acc = repo.findByEmail(email).orElseThrow(() -> new RuntimeException("Account not found"));
        if (dto.eur != null) acc.setEur(dto.eur);
        if (dto.usd != null) acc.setUsd(dto.usd);
        if (dto.gbp != null) acc.setGbp(dto.gbp);
        if (dto.chf != null) acc.setChf(dto.chf);
        if (dto.rsd != null) acc.setRsd(dto.rsd);
        return repo.save(acc);
    }

    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }
}
