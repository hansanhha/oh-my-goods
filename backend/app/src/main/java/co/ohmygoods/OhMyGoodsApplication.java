package co.ohmygoods;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * todo coupon 도메인, 서비스 구현
 * todo file 도메인, 서비스 구현 --
 * todo review 도메인 구현
 * todo order service 구현
 * todo payment service 리팩토링
 * todo seller 도메인, 서비스 리팩토링
 * todo community 도메인 구현
 * todo auth 권한 기능 수정
 * todo seller 쿠폰 발급 대상 지정 및 쿠폰 발급 로직 수정
 * todo 정산 도메인 설계 및 구현
 * todo 모든 도메인 컨트롤러, 멱등성(필요 부분), 에러 처리
 * todo 유닛/통합 테스트
 * todo 캐싱(레디스)
 * todo 로깅, api 문서화
 * todo docker
 * todo aws
 * todo ci,cd
 * todo cars 패턴 적용
 * todo ddd 및 port/adapter 패턴 적용(새 브랜치)
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
