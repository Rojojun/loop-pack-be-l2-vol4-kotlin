package com.loopers.interfaces.api.coupon

import com.loopers.application.coupon.CouponFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/coupons")
class CouponV1Controller(
    private val couponFacade: CouponFacade,
) : CouponV1ApiSpec {
    @PostMapping("/{couponId}/issue")
    override fun issueCoupon(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @PathVariable couponId: Long,
    ): ApiResponse<CouponV1Dto.CouponResponse> {
        val info = couponFacade.issueCoupon(couponId = couponId, loginId = loginId)
        val response = CouponV1Dto.CouponResponse.from(info)
        return ApiResponse.success(response)
    }

    @GetMapping("/me")
    override fun getCoupons(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
    ): ApiResponse<List<CouponV1Dto.CouponResponse>> {
        val info = couponFacade.getCoupons(loginId)
        val response = info.map { CouponV1Dto.CouponResponse.from(it) }
        return ApiResponse.success(response)
    }
}
