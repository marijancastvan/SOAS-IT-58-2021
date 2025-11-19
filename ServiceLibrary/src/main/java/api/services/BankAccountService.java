package api.services;

import java.util.List;
import api.dtos.BankAccountDto;

public interface BankAccountService {
    List<BankAccountDto> getAll();
    BankAccountDto getByEmail(String email);
    BankAccountDto createForUser(String email);
    BankAccountDto update(String email, BankAccountDto dto);
    void deleteByEmail(String email);
}
