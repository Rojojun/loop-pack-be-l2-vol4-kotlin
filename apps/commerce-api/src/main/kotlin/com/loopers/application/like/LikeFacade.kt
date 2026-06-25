package com.loopers.application.like

import com.loopers.domain.like.LikeResult
import com.loopers.domain.like.LikeService
import com.loopers.domain.like.ProductId
import com.loopers.domain.product.ProductService
import com.loopers.domain.user.UserService
import org.springframework.stereotype.Component

@Component
class LikeFacade(
    private val likeService: LikeService,
    private val productService: ProductService,
    private val userService: UserService
) {
    /**
     * 좋아요 등록. 신규 생성 시 true, 이미 존재 시 false (멱등 no-op).
     */
    fun addLike(loginId: String, productId: Long): Boolean {
        val user = userService.getByLoginId(loginId)
        val product = productService.getProduct(productId)
        return likeService.addLike(user.id, product.id) is LikeResult.Liked
    }

    /**
     * 좋아요 취소. 존재 시 삭제, 미존재 시 no-op. 결과 무관 항상 정상 종료.
     */
    fun removeLike(loginId: String, productId: Long): Unit {
        val user = userService.getByLoginId(loginId)
        val product = productService.getProduct(productId)

        likeService.remove(user.id, product.id)
    }

    fun findLikes(requestLoginId: String, pathUserId: Long): List<LikedProductInfo> {
        val user = userService.getByLoginId(requestLoginId)
        user.validateSelf(pathUserId)

        val likes = likeService.getLikeByUserId(user.id).filter { it.available() }
        val productIds = likes.map { like -> like.productId }
        val likeCounts = likeService.getLikeCountGroupByProductId(productIds)

        val products = productService.getProducts(productIds)
            .associateBy { it.id }

        return likes.map {
            val product = products.getValue(it.productId)
            val likeCount = likeCounts[ProductId(product.id)]
            LikedProductInfo.of(it, product, likeCount)
        }
    }
}
