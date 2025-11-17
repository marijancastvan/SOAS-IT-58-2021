package com.example.bankaccount;

import java.util.List;

import api.dtos.BankAccountDto;

public interface BankAccountService {
    List<BankAccountModel> getAll();
    BankAccountModel getByEmail(String email);
    BankAccountModel createForUser(String email);
    BankAccountModel update(String email, BankAccountDto dto);
    void deleteByEmail(String email);
}
