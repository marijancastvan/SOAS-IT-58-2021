package com.example.cryptowallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.UsersServiceProxy;

@Service
@Transactional
public class CryptoWalletServiceImpl implements CryptoWalletService {

    private final CryptoWalletRepository repo;
    private final UsersServiceProxy usersProxy;

    public CryptoWalletServiceImpl(CryptoWalletRepository repo, UsersServiceProxy usersProxy) {
        this.repo = repo;
        this.usersProxy = usersProxy;
    }

    @Override
    public List<CryptoWalletModel> getAll() {
        return repo.findAll();
    }

    @Override
    public CryptoWalletModel getByEmail(String email) {
        return repo.findByEmail(email)
                   .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    @Override
    public CryptoWalletModel createForUser(String email) {
        UserDto user = usersProxy.getUserByEmail(email);
        if (user == null) throw new RuntimeException("Related user not found");
        if (!"USER".equalsIgnoreCase(user.getRole()))
            throw new RuntimeException("Crypto wallet allowed only for role USER");

        if (repo.existsByEmail(email)) throw new RuntimeException("Wallet already exists");

        CryptoWalletModel wallet = new CryptoWalletModel(email);
        return repo.save(wallet);
    }

    @Override
    public CryptoWalletModel update(String email, CryptoWalletDto dto) {
        CryptoWalletModel wallet = repo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (dto.btc != null) wallet.setBtc(dto.btc);
        if (dto.eth != null) wallet.setEth(dto.eth);
        if (dto.ada != null) wallet.setAda(dto.ada);

        return repo.save(wallet);
    }

    @Override
    public void deleteByEmail(String email) {
        repo.deleteByEmail(email);
    }
}
