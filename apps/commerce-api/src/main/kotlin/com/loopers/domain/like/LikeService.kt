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
        val likeModel = LikeModel.of(userId, productId)
        val existLike = likeRepository.findByUserIdAndProductId(userId, productId).firstOrNull()

        return when {
            existLike == null -> {
                likeRepository.save(likeModel)
                true
            }
            existLike.available() -> false
            else -> {
                existLike.restore()
                true
            }
        }
    }

    fun remove(userId: Long, productId: Long) {
        val likeModel = likeRepository.findByUserIdAndProductId(userId, productId)
        if (likeModel.isEmpty()) {
            return
        }
        if (likeModel.size != 1) {
            return
        }
        likeModel.first().delete()
    }

    fun getLikeByUserId(userId: Long): List<LikeModel> {
        return likeRepository.findAllByUserId(userId)
    }
}
