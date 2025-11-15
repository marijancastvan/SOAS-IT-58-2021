package com.example.bankaccount;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccountModel, Long> {
    Optional<BankAccountModel> findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}
