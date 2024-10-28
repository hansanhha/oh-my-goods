package co.ohmygoods.auth.jwt.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String tokenValue;

    @Column(nullable = false)
    private String subject;

    @Column(unique = true, nullable = false, updatable = false)
    private String jwtId;

    @Column(nullable = false)
    private String issuer;

    @Column(nullable = false, updatable = false)
    private String audience;

    @Column(nullable = false, updatable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expiresIn;
}
