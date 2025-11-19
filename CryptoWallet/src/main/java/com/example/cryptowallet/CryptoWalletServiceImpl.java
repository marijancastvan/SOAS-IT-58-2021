package com.example.cryptowallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import api.dtos.CryptoWalletDto;
import api.dtos.UserDto;
import api.proxies.UsersServiceProxy;
import api.services.CryptoWalletService;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CryptoWalletServiceImpl implements CryptoWalletService {

    private final CryptoWalletRepository repo;
    private final UsersServiceProxy usersProxy;

    public CryptoWalletServiceImpl(CryptoWalletRepository repo, UsersServiceProxy usersProxy) {
        this.repo = repo;
        this.usersProxy = usersProxy;
    }

    // --- DTO konverzija ---
    private CryptoWalletDto convertModelToDto(CryptoWalletModel model) {
        CryptoWalletDto dto = new CryptoWalletDto();
        dto.email = model.getEmail();
        dto.btc = model.getBtc();
        dto.eth = model.getEth();
        dto.ada = model.getAda();
        return dto;
    }

    private CryptoWalletModel convertDtoToModel(CryptoWalletDto dto) {
        CryptoWalletModel model = new CryptoWalletModel();
        model.setEmail(dto.email);
        model.setBtc(dto.btc != null ? dto.btc : BigDecimal.ZERO);
        model.setEth(dto.eth != null ? dto.eth : BigDecimal.ZERO);
        model.setAda(dto.ada != null ? dto.ada : BigDecimal.ZERO);
        return model;
    }

    // --- CRUD metode sa autorizacijom ---
    @Override
    public List<CryptoWalletDto> getAll(String userRole) {
        if ("USER".equalsIgnoreCase(userRole))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER cannot view all wallets");

        return repo.findAll().stream()
                .map(this::convertModelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CryptoWalletDto getByEmail(String email, String userRole, String requesterEmail) {
        CryptoWalletModel wallet = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if ("USER".equalsIgnoreCase(userRole) && !wallet.getEmail().equals(requesterEmail))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER can view only their own wallet");

        if ("OWNER".equalsIgnoreCase(userRole))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OWNER not authorized");

        return convertModelToDto(wallet);
    }

    @Override
    public CryptoWalletDto createForUser(String email) {
        if (email == null || !email.contains("@"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email");

        UserDto user = usersProxy.getUserByEmail(email);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        if (!"USER".equalsIgnoreCase(user.getRole()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only USER role can have wallet");

        if (repo.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Wallet already exists");

        CryptoWalletModel wallet = new CryptoWalletModel();
        wallet.setEmail(email);
        wallet.setBtc(BigDecimal.ZERO);
        wallet.setEth(BigDecimal.ZERO);
        wallet.setAda(BigDecimal.ZERO);

        return convertModelToDto(repo.save(wallet));
    }

    @Override
    public CryptoWalletDto update(String email, CryptoWalletDto dto, String userRole, String requesterEmail) {
        CryptoWalletModel wallet = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if ("USER".equalsIgnoreCase(userRole) && !wallet.getEmail().equals(requesterEmail))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER cannot update other wallets");

        if ("OWNER".equalsIgnoreCase(userRole))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OWNER not authorized");

        if (dto.btc != null) wallet.setBtc(dto.btc);
        if (dto.eth != null) wallet.setEth(dto.eth);
        if (dto.ada != null) wallet.setAda(dto.ada);

        return convertModelToDto(repo.save(wallet));
    }

    @Override
    public void deleteByEmail(String email) {
        if (!repo.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found");

        repo.deleteByEmail(email);
    }

    // --- Deposit / Withdraw ---
    @Override
    public CryptoWalletDto deposit(String email, String currency, BigDecimal amount, String userRole, String requesterEmail) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");

        CryptoWalletModel wallet = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if ("USER".equalsIgnoreCase(userRole) && !wallet.getEmail().equals(requesterEmail))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER cannot deposit to other wallets");

        if ("OWNER".equalsIgnoreCase(userRole))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OWNER not authorized");

        switch (currency.toUpperCase()) {
            case "BTC" -> wallet.setBtc(wallet.getBtc().add(amount));
            case "ETH" -> wallet.setEth(wallet.getEth().add(amount));
            case "ADA" -> wallet.setAda(wallet.getAda().add(amount));
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency");
        }

        return convertModelToDto(repo.save(wallet));
    }

    @Override
    public CryptoWalletDto withdraw(String email, String currency, BigDecimal amount, String userRole, String requesterEmail) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");

        CryptoWalletModel wallet = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Wallet not found"));

        if ("USER".equalsIgnoreCase(userRole) && !wallet.getEmail().equals(requesterEmail))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "USER cannot withdraw from other wallets");

        if ("OWNER".equalsIgnoreCase(userRole))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "OWNER not authorized");

        switch (currency.toUpperCase()) {
            case "BTC" -> {
                if (wallet.getBtc().compareTo(amount) < 0)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
                wallet.setBtc(wallet.getBtc().subtract(amount));
            }
            case "ETH" -> {
                if (wallet.getEth().compareTo(amount) < 0)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
                wallet.setEth(wallet.getEth().subtract(amount));
            }
            case "ADA" -> {
                if (wallet.getAda().compareTo(amount) < 0)
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds");
                wallet.setAda(wallet.getAda().subtract(amount));
            }
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency");
        }

        return convertModelToDto(repo.save(wallet));
    }
}
