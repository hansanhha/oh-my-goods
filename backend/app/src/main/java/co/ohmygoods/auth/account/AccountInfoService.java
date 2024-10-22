package co.ohmygoods.auth.account;

import co.ohmygoods.auth.account.model.Account;
import co.ohmygoods.auth.account.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountInfoService {

    private final AccountRepository accountRepository;

    public Optional<Long> findIdByEmail(String email) {
        return accountRepository.findByEmail(email).map(Account::getId);
    }

    public Long signUp(SignUpInfo signUpInfo) {
        var newAccountInfo = Account.builder()
                .nickname(UUID.randomUUID().toString())
                .oauth2Vendor(signUpInfo.vendor())
                .oauth2MemberId(signUpInfo.oauth2MemberId())
                .email(signUpInfo.email())
                .role(Role.USER)
                .build();

        var newAccount = accountRepository.save(newAccountInfo);
        return newAccount.getId();
    }
}
