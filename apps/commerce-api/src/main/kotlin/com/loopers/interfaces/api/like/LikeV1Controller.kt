package com.loopers.interfaces.api.like

import com.loopers.application.like.LikeFacade
import com.loopers.interfaces.api.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

/**
 * Like는 URI prefix가 두 가지(`/api/v1/products/.../likes` + `/api/v1/users/.../likes`)라
 * 클래스 레벨 @RequestMapping 없이 메서드별로 경로를 명시.
 */
@RestController
class LikeV1Controller(
    private val likeFacade: LikeFacade,
) : LikeV1ApiSpec {

    @PostMapping("/api/v1/products/{productId}/likes")
    override fun addLike(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @PathVariable productId: Long,
    ): ResponseEntity<ApiResponse<Any>> {
        val created: Boolean = likeFacade.addLike(loginId, productId)
        // 신규 등록: 201, 이미 좋아요 상태: 200 (멱등 no-op)
        val status = if (created) HttpStatus.CREATED else HttpStatus.OK
        return ResponseEntity.status(status).body(ApiResponse.success())
    }

    @DeleteMapping("/api/v1/products/{productId}/likes")
    override fun removeLike(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @PathVariable productId: Long,
    ): ResponseEntity<Void> {
        likeFacade.removeLike(loginId, productId)
        // 존재 여부 무관 항상 204 (멱등)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/api/v1/users/{userId}/likes")
    override fun findLikes(
        @RequestHeader("X-Loopers-LoginId") loginId: String,
        @PathVariable userId: Long,
    ): ApiResponse<List<LikeV1Dto.LikedProductResponse>> {
        val list = likeFacade.findLikes(loginId, userId)
            .map { LikeV1Dto.LikedProductResponse.from(it) }
        return ApiResponse.success(list)
    }
}
