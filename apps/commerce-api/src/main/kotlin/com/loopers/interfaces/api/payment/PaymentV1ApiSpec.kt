package com.loopers.interfaces.api.payment

import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "Payment V1 API", description = "Order 도메인 결제 API 입니다.")
interface PaymentV1ApiSpec {
    fun pay(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "로그인 PW") @RequestHeader("X-Loopers-LoginPw") loginPassword: String,
        @Schema(name = "결제 요청 DTO") @RequestBody request: PaymentV1Dto.PaymentRequest,
    )
}
