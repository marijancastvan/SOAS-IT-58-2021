package com.example.cryptowallet;

import java.util.List;

import api.dtos.CryptoWalletDto;

public interface CryptoWalletService {
    List<CryptoWalletModel> getAll();
    CryptoWalletModel getByEmail(String email);
    CryptoWalletModel createForUser(String email);
    CryptoWalletModel update(String email, CryptoWalletDto dto);
    void deleteByEmail(String email);
}
