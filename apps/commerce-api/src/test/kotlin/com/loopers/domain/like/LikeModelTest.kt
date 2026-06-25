package com.loopers.domain.like

import com.loopers.fixture.LikeModelFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.assertj.core.api.Assertions.assertThat

internal class LikeModelTest {

    @DisplayName("LikeModel.from 로 생성하면")
    @Nested
    internal inner class Create {
        @DisplayName("전달한 userId, productId 가 그대로 세팅되고 likedAt 이 채워진다.")
        @Test
        fun createSuccess() {
            // given
            val fixture = LikeModelFixture.custom(userId = 7L, productId = 42L)

            // when
            val likeModel = fixture.toModel()

            // then
            assertNotNull(likeModel)
            assertEquals(7L, likeModel.userId)
            assertEquals(42L, likeModel.productId)
            assertNotNull(likeModel.likedAt)
        }

        @DisplayName("생성 직후에는 삭제되지 않았으므로 available 하다.")
        @Test
        fun availableWhenCreated() {
            // given
            val likeModel = LikeModelFixture.defaults().toModel()

            // when then
            assertThat(likeModel.available()).isTrue()
        }
    }

    @DisplayName("LikeModel 의 soft delete 흐름")
    @TestFactory
    fun softDeleteFlow(): Collection<DynamicTest> {
        lateinit var likeModel: LikeModel
        return listOf(
            DynamicTest.dynamicTest("1. 생성 직후에는 available() 가 true 이다.") {
                likeModel = LikeModelFixture.defaults().toModel()
                assertThat(likeModel.available()).isTrue()
            },
            DynamicTest.dynamicTest("2. delete() 하면 available() 가 false 가 된다.") {
                likeModel.delete()
                assertThat(likeModel.available()).isFalse()
            },
            DynamicTest.dynamicTest("3. delete() 는 멱등하므로 다시 호출해도 여전히 available() 가 false 이다.") {
                likeModel.delete()
                assertThat(likeModel.available()).isFalse()
            },
            DynamicTest.dynamicTest("4. restore() 하면 다시 available() 가 true 가 된다.") {
                likeModel.restore()
                assertThat(likeModel.available()).isTrue()
            },
        )
    }
}
