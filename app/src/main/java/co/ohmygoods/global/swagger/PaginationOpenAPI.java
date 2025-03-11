package co.ohmygoods.global.swagger;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

public interface PaginationOpenAPI {

    @Parameter(in = ParameterIn.QUERY, name = "조회할 페이지 수", description = "기본값: 0")
    @interface PageDescription {

    }

    @Parameter(in = ParameterIn.QUERY, name = "조회할 개수", description = "기본값: 20")
    @interface SizeDescription {

    }
}
