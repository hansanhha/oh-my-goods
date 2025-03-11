package co.ohmygoods.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RFC 9457 ProblemDetail 형식을 따르는 오류 응답")
public record SimpleProblemDetail(

        @Schema(description = "HTTP 상태 코드", example = "404")
        int status,

        @Schema(description = "오류에 대한 간단한 설명", example = "Not Found Account")
        String  title,

        @Schema(description = "오류에 대한 자세한 설명", example = "아이디 '1'의 계정을 찾을 수 없습니다")
        String detail,

        @Schema(description = "오류의 URI 참조", example = "https://localhost:8080/accounts/1")
        String type,

        @Schema(description = "오류 인스턴스에 대한 URI", example = "/accounts/1")
        String instance){

}
