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
    private String id;

    @Column(nullable = false, updatable = false, columnDefinition = "TEXT")
    private String tokenValue;

    public static RefreshToken create(String email, String tokenValue) {
        return new RefreshToken(email, tokenValue);
    }

    public void updateTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }
}
