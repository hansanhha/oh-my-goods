package co.ohmygoods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * todo delivery 패키지 구현
 * todo 웹, 에러 처리
 * todo 유닛/통합 테스트
 * todo api 문서화
 * todo docker
 * todo aws
 * todo 프론트
 */
@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
public class OhMyGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OhMyGoodsApplication.class, args);
    }
}
