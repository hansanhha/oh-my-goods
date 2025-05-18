package co.ohmygoods.auth.account.model.vo;

/**
 * 유효한 토큰을 보유한 사용자의 정보를 나타내는 객체
 *
 * @param memberId 애플리케이션 end-user 식별값
 * @param role 계정 Role (spring security RBAC)
 */
public record AuthenticatedAccount(
        String jwt,
        String memberId,
        Role role) {

}
