package co.ohmygoods.auth.account;

import co.ohmygoods.auth.account.dto.OAuth2AccountDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Optional<OAuth2AccountDTO> getOne(String email) {
        return accountRepository.findByEmail(email)
                .map(OAuth2AccountDTO::from);
    }
}