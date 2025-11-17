package com.example.cryptowallet;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CryptoWalletRepository extends JpaRepository<CryptoWalletModel, Long> {
    Optional<CryptoWalletModel> findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}
