package com.example.bankaccount;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccountModel, Integer> {

	BankAccountModel findByEmail(String email);
	
}