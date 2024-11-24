package co.ohmygoods.payment.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@RestClientTest(KakaopayService.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class KakaopayServiceTest {

    @InjectMocks
    private KakaopayService kakaopayService;


    @Test
    void 결제_준비_성공() {
        kakaopayService.ready()
    }

    @Test
    void 결제_승인_성공() {

    }

    @Test
    void Order가_준비되지_않으면_결제_준비_실패() {

    }

    @Test
    void 결제_준비_외부_API_요청_실패() {

    }

    @Test
    void 결제_승인_외부_API_요청_실패() {

    }

}