package com.loopers.domain.like

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class LikeService(
    private val likeRepository: LikeRepository
) {
    fun getLikeCount(productId: Long): Int {
        return likeRepository.findAllByProductId(productId).count { it.available() }
    }

    fun getLikeCountGroupByProductId(productModelIds: List<Long>): Map<ProductId, LikeCount> {
        return likeRepository.findAllByProductIdIn(productModelIds)
            .filter { it.available() }
            .groupingBy { ProductId(it.productId) }
            .eachCount()
            .mapValues { LikeCount(it.value) }
    }

    fun addLike(userId: Long, productId: Long): Boolean {
        val existValue = likeRepository.findByUserIdAndProductId(userId, productId)
        val likeModel = existValue ?: LikeModel.of(userId, productId)
        val isNewLikeModel = existValue == null || !existValue.available()

        likeModel.like()
        likeRepository.save(likeModel)

        return isNewLikeModel
    }

    fun remove(userId: Long, productId: Long) {
        val likeModel = likeRepository.findByUserIdAndProductId(userId, productId) ?: return
        likeModel.delete()
    }

    fun getLikeByUserId(userId: Long): List<LikeModel> {
        return likeRepository.findAllByUserId(userId)
    }
}
