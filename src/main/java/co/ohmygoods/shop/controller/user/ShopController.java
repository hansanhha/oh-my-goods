package co.ohmygoods.shop.controller.user;

import co.ohmygoods.shop.service.user.ShopService;
import co.ohmygoods.shop.service.admin.dto.ShopOverviewResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "상점", description = "상점 조회 관련 api")
@RequestMapping("/api/shop")
@RestController
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @Operation(summary = "상점 조회", description = "특정 상점을 조회합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상점 정보 반환")
    })
    @GetMapping("/{shopId}")
    public ShopOverviewResponse getShop(@Parameter(name = "조회할 상점 아이디", in = ParameterIn.PATH) @PathVariable Long shopId) {
        return shopService.getShopOverview(shopId);
    }
}
