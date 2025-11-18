package com.example.cryptowallet;

import api.dtos.CryptoWalletDto;
import java.math.BigDecimal;
import java.util.List;

public interface CryptoWalletService {

    List<CryptoWalletDto> getAll(String userRole);

    CryptoWalletDto getByEmail(String email, String userRole, String requesterEmail);

    CryptoWalletDto createForUser(String email);

    CryptoWalletDto update(String email, CryptoWalletDto dto, String userRole, String requesterEmail);

    void deleteByEmail(String email);

    CryptoWalletDto deposit(String email, String currency, BigDecimal amount, String userRole, String requesterEmail);

    CryptoWalletDto withdraw(String email, String currency, BigDecimal amount, String userRole, String requesterEmail);
}
