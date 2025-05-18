package co.ohmygoods.auth.jwt.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    private Long id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String tokenValue;

    public static RefreshToken create(String memberId, String tokenValue) {
        return new RefreshToken(0L, memberId, tokenValue);
    }

    public void updateTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
