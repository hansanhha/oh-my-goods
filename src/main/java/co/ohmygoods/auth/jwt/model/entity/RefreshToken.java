package co.ohmygoods.auth.jwt.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
