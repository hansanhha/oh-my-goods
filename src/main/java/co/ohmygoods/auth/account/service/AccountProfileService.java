package co.ohmygoods.auth.account.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.ohmygoods.auth.account.model.entity.Account;
import co.ohmygoods.auth.account.repository.AccountRepository;
import co.ohmygoods.auth.account.service.dto.AccountProfile;
import co.ohmygoods.auth.account.service.dto.ProfileNicknameUpdateInfo;
import co.ohmygoods.auth.exception.AuthException;
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

    public ProfileNicknameUpdateInfo updateNickname(String memberId, String newNickname) {
        Account account = accountRepository.findByMemberId(memberId).orElseThrow(AuthException::notFoundAccount);
        String nickname = account.getNickname();

        account.updateNicknameWithValidation(newNickname);

        return new ProfileNicknameUpdateInfo(nickname, newNickname);
    }

}
