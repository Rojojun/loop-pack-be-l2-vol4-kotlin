package com.loopers.domain.brand

import com.loopers.fixture.BrandModelFixture
import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class BrandServiceTest {
    private val inMemoryBrandRepository = InMemoryBrandRepository()
    private val brandService = BrandService(inMemoryBrandRepository)

    @DisplayName("브랜드를 생성할 때")
    @Nested
    internal inner class Create {
        @DisplayName("이름을 받아 ACTIVE 상태의 브랜드를 저장한다.")
        @Test
        fun createBrand() {
            // given
            val defaults = BrandModelFixture.defaults()

            // when
            val result = brandService.createBrandModel(defaults.name)

            // then
            assertNotNull(result)
            assertEquals(defaults.name, result.name)
            assertEquals(BrandStatus.ACTIVE, result.status)
        }
    }

    @DisplayName("브랜드를 단건 조회할 때")
    @Nested
    internal inner class GetBrand {
        private var existId = 0L

        @BeforeEach
        fun init() {
            val saved = inMemoryBrandRepository.save(BrandModelFixture.defaults().toModel())
            existId = saved.id
        }

        @DisplayName("존재하는 ID라면 브랜드를 반환한다.")
        @Test
        fun getBrandSuccess() {
            // when
            val result = brandService.getBrand(existId)

            // then
            assertNotNull(result)
            assertEquals(existId, result.id)
        }

        @DisplayName("존재하지 않는 ID라면 NOT_FOUND 예외를 던진다.")
        @Test
        fun getBrandNotFound() {
            // when then
            assertThatThrownBy { brandService.getBrand(999L) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("999 에 해당하는 브랜드가 존재하지 않습니다.")
        }
    }

    @DisplayName("활성화된 브랜드를 조회할 때")
    @Nested
    internal inner class GetBrandActive {
        @DisplayName("ACTIVE 상태의 브랜드는 정상적으로 반환한다.")
        @Test
        fun getBrandActiveSuccess() {
            // given
            val saved = inMemoryBrandRepository.save(BrandModelFixture.defaults().toModel())

            // when
            val result = brandService.getBrandActive(saved.id)

            // then
            assertNotNull(result)
            assertEquals(BrandStatus.ACTIVE, result.status)
        }

        @DisplayName("CLOSED 상태의 브랜드는 BAD_REQUEST 예외를 던진다.")
        @Test
        fun getBrandActiveClosed() {
            // given
            val closed = BrandModelFixture.defaults().toModel()
            closed.statusChange(BrandStatus.CLOSED)
            val saved = inMemoryBrandRepository.save(closed)

            // when then
            assertThatThrownBy { brandService.getBrandActive(saved.id) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("브랜드가 활성화되어 있지 않습니다.")
        }

        @DisplayName("DELETED 상태의 브랜드는 BAD_REQUEST 예외를 던진다.")
        @Test
        fun getBrandActiveDeleted() {
            // given
            val deleted = BrandModelFixture.defaults().toModel()
            deleted.statusChange(BrandStatus.DELETED)
            val saved = inMemoryBrandRepository.save(deleted)

            // when then
            assertThatThrownBy { brandService.getBrandActive(saved.id) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("브랜드가 활성화되어 있지 않습니다.")
        }
    }

    @DisplayName("브랜드를 수정할 때")
    @Nested
    internal inner class UpdateBrand {
        private var existId = 0L

        @BeforeEach
        fun init() {
            val saved = inMemoryBrandRepository.save(BrandModelFixture.custom("수정전이름").toModel())
            existId = saved.id
        }

        @DisplayName("존재하는 브랜드의 이름을 변경한다.")
        @Test
        fun updateBrandSuccess() {
            // when
            val result = brandService.updateBrand(existId, "수정후이름")

            // then
            assertEquals("수정후이름", result.name)
            assertEquals("수정후이름", brandService.getBrand(existId).name)
        }

        @DisplayName("존재하지 않는 브랜드를 수정하면 NOT_FOUND 예외를 던진다.")
        @Test
        fun updateBrandNotFound() {
            // when then
            assertThatThrownBy { brandService.updateBrand(999L, "수정후이름") }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("999 에 해당하는 브랜드가 존재하지 않습니다.")
        }
    }

    @DisplayName("브랜드를 삭제할 때")
    @Nested
    internal inner class DeleteBrand {
        private var existId = 0L

        @BeforeEach
        fun init() {
            val saved = inMemoryBrandRepository.save(BrandModelFixture.defaults().toModel())
            existId = saved.id
        }

        @DisplayName("soft delete 되어 deletedAt이 채워지고 status가 DELETED로 변경된다.")
        @Test
        fun deleteSuccess() {
            // given
            val before = brandService.getBrand(existId)
            assertNull(before.deletedAt)

            // when
            brandService.delete(existId)

            // then
            val after = brandService.getBrand(existId)
            assertNotNull(after.deletedAt)
            assertEquals(BrandStatus.DELETED, after.status)
        }

        @DisplayName("존재하지 않는 브랜드를 삭제하면 NOT_FOUND 예외를 던진다.")
        @Test
        fun deleteNotFound() {
            // when then
            assertThatThrownBy { brandService.delete(999L) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("999 에 해당하는 브랜드가 존재하지 않습니다.")
        }
    }

    @DisplayName("여러 브랜드를 ID 목록으로 조회할 때")
    @Nested
    internal inner class GetBrandsByIds {
        private var firstId = 0L
        private var secondId = 0L

        @BeforeEach
        fun init() {
            firstId = inMemoryBrandRepository.save(BrandModelFixture.custom("브랜드1").toModel()).id
            secondId = inMemoryBrandRepository.save(BrandModelFixture.custom("브랜드2").toModel()).id
        }

        @DisplayName("요청한 ID들에 해당하는 브랜드를 id 기준 Map으로 반환한다.")
        @Test
        fun getBrandsByIdsSuccess() {
            // when
            val result = brandService.getBrandsByIds(listOf(firstId, secondId))

            // then
            assertEquals(2, result.size)
            assertEquals("브랜드1", result[firstId]?.name)
            assertEquals("브랜드2", result[secondId]?.name)
        }

        @DisplayName("존재하지 않는 ID는 결과 Map에 포함되지 않는다.")
        @Test
        fun getBrandsByIdsPartial() {
            // when
            val result = brandService.getBrandsByIds(listOf(firstId, 999L))

            // then
            assertEquals(1, result.size)
            assertThat(result.keys).containsExactly(firstId)
            assertNull(result[999L])
        }

        @DisplayName("빈 ID 목록을 넘기면 빈 Map을 반환한다.")
        @Test
        fun getBrandsByIdsEmpty() {
            // when
            val result = brandService.getBrandsByIds(emptyList())

            // then
            assertThat(result).isEmpty()
        }
    }
}
