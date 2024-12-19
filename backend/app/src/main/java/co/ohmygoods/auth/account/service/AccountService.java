package co.ohmygoods.auth.account.service;

import co.ohmygoods.auth.account.service.dto.AccountResponse;
import co.ohmygoods.auth.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Optional<AccountResponse> getOne(String email) {
        return accountRepository.findByEmail(email)
                .map(AccountResponse::from);
    }
}
