package co.ohmygoods.auth.account.service;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountProfile;
import co.ohmygoods.auth.exception.AuthException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountProfileService {

    private final AccountRepository accountRepository;

    public AccountProfile getAccountProfile(String memberId) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        return AccountProfile.from(account);
    }

}
