package com.example.bankaccount;

import java.util.List;

public interface BankAccountService {
    List<BankAccountModel> getAll();
    BankAccountModel getByEmail(String email);
    BankAccountModel createForUser(String email);
    BankAccountModel update(String email, BankAccountDto dto);
    void deleteByEmail(String email);
}
