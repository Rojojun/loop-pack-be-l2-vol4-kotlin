package com.loopers.application.like

import org.springframework.stereotype.Component

@Component
class LikeFacade {
    /**
     * 좋아요 등록. 신규 생성 시 true, 이미 존재 시 false (멱등 no-op).
     */
    fun addLike(loginId: String, productId: Long): Boolean =
        TODO("LikeService.checkLikeExists → 없으면 createLike + ProductService.incrementLikeCount")

    /**
     * 좋아요 취소. 존재 시 삭제, 미존재 시 no-op. 결과 무관 항상 정상 종료.
     */
    fun removeLike(loginId: String, productId: Long): Unit =
        TODO("LikeService.findLikeModel → 있으면 deleteLike + ProductService.decrementLikeCount")

    fun findLikes(requestLoginId: String, pathUserId: Long): List<LikedProductInfo> =
        TODO("본인 검증 + LikeService.findLikesByUserId + ProductService 조합")
}