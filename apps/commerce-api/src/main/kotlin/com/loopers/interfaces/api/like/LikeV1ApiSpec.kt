package com.loopers.interfaces.api.like

import com.loopers.interfaces.api.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@Tag(name = "Like V1 API", description = "Like 도메인 API 입니다. (완전 멱등)")
interface LikeV1ApiSpec {
    @Operation(summary = "좋아요 등록 (멱등)", description = "신규 201, 중복 200 (no-op).")
    fun addLike(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "상품 ID") @PathVariable productId: Long,
    ): ResponseEntity<ApiResponse<Any>>

    @Operation(summary = "좋아요 취소 (멱등)", description = "존재/미존재 무관 항상 204.")
    fun removeLike(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "상품 ID") @PathVariable productId: Long,
    ): ResponseEntity<Void>

    @Operation(summary = "내 좋아요 목록", description = "본인의 좋아요 목록만 조회 가능.")
    fun findLikes(
        @Schema(name = "로그인 ID") @RequestHeader("X-Loopers-LoginId") loginId: String,
        @Schema(name = "유저 ID") @PathVariable userId: Long,
    ): ApiResponse<List<LikeV1Dto.LikedProductResponse>>
}