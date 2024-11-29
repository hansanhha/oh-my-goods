package co.ohmygoods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * todo seller 도메인 리팩토링
 * todo coupon 도메인 개발
 * todo 정산 도메인 설계 및 개발
 * todo 컨트롤러, 멱등성, 에러 처리
 * todo 유닛/통합 테스트
 * todo 캐싱(레디스)
 * todo 로깅, api 문서화
 * todo docker
 * todo aws
 * todo ci,cd
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
