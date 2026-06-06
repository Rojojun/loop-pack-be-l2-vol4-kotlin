package com.loopers.concurrency

import com.loopers.application.like.LikeFacade
import com.loopers.domain.like.LikeService
import com.loopers.domain.order.OrderRepository
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.ProductModel.Companion.of
import com.loopers.domain.product.TechCategory
import com.loopers.domain.user.UserModel
import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.infrastructure.product.ProductJpaRepository
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import kotlin.test.AfterTest

@SpringBootTest
class LikeConcurrencyTest @Autowired constructor(
    private val likeFacade: LikeFacade,
    private val likeService: LikeService,
    private val userJpaRepository: UserJpaRepository,
    private val productJpaRepository: ProductJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp
) {
    @AfterTest
    fun testDown() = databaseCleanUp.truncateAllTables()

    private fun saveProduct(): ProductModel =
        productJpaRepository.save(
            of(
                brandId = 1L,
                isbn = "isbn-like",
                name = "테스트 상품",
                authName = "저자",
                techCategory = TechCategory.BACKEND,
                level = Level.BEGINNER,
                price = 10000.0,
                description = "설명",
            ),
        )

    private fun saveUser(loginId: String): UserModel =
        userJpaRepository.save(
            UserModel.of(loginId, "테스터", "test_1234", BirthVO(LocalDate.of(1993, 3, 16)), EmailVO("test@test.com")) { it },
        )

    @DisplayName("여러 유저가 같은 상품에 동시에 좋아요를 눌러도 좋아요 수가 정확히 인원수만큼 반영된다")
    @Test
    fun concurrentLikes_fromDifferentUsers_countsAll() {
        // given
        val product = saveProduct()
        val userCount = 10
        val users = (1..userCount).map { saveUser("user$it") }
        
        // when
        val results = runConcurrently(userCount) { index ->
            likeFacade.addLike(users[index].loginId, product.id)
        }
        
        // then
        assertThat(likeService.getLikeCount(product.id)).isEqualTo(userCount)
    }

    @Disabled("좋아요 중복 방지는 UK가 필요(phantom insert는 락으로 불가). UK 도입 보류 결정 — 결함 재현용으로 보존")
    @DisplayName("같은 유저가 같은 상품에 동시에 좋아요를 여러 번 눌러도 좋아요 수는 1이다.")
    @Test
    fun concurrentLikes_fromSameUser_isIdempotent() {
        // given
        val product = saveProduct()
        val user = saveUser("testId")
        val threadCount = 10
        
        // when
        runConcurrently(threadCount) {
            likeFacade.addLike(user.loginId, product.id)
        }
        
        // then
        assertThat(likeService.getLikeCount(product.id)).isEqualTo(1)
    }
}
