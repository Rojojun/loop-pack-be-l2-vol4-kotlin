package com.loopers.domain.user

import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.fixture.UserModelFixture
import com.loopers.support.error.CoreException
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class UserServiceTest {
    private val inMemoryUserRepository = InMemoryUserRepository()
    private val userService = UserService(inMemoryUserRepository)

    @DisplayName("유저의 정보를 받아서 유저를 저장한다.")
    @Test
    fun saveSuccess() {
        // given
        val defaults = UserModelFixture.defaults()

        // when
        val result = userService.createUserModel(
            defaults.loginId,
            defaults.name,
            defaults.password,
            BirthVO(defaults.birth),
            EmailVO(defaults.email),
        ) { it }

        // then
        assertNotNull(result)
        assertEquals(defaults.loginId, result.loginId)
    }

    @DisplayName("유저의 중복체크를 할 때")
    @Nested
    internal inner class Duplicate {
        private lateinit var expectedId: String

        @BeforeEach
        fun init() {
            val duplicate = UserModelFixture.duplicate()
            val userModel = UserModel.of(
                duplicate.loginId,
                duplicate.name,
                duplicate.password,
                BirthVO(duplicate.birth),
                EmailVO(duplicate.email),
            ) { it }

            expectedId = duplicate.loginId
            inMemoryUserRepository.save(userModel)
        }

        @DisplayName("중복된 유저는 True를 반환한다.")
        @Test
        fun duplicate() {
            assertThatThrownBy { userService.checkLoginIdDuplication(expectedId) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("이미 존재하는 유저의 아이디입니다.")
        }

        @DisplayName("중복 되지 않은 유저는 통과한다.")
        @Test
        fun notDuplicate() {
            userService.checkLoginIdDuplication("tester_new")
        }
    }

    @DisplayName("유저의 id로 유저를 조회할 때")
    @Nested
    internal inner class ReadSingular {
        private var existSequence = 0L

        @BeforeEach
        fun init() {
            val defaults = UserModelFixture.defaults()
            val userModel = inMemoryUserRepository.save(
                UserModel.of(
                    defaults.loginId,
                    defaults.name,
                    defaults.password,
                    BirthVO(defaults.birth),
                    EmailVO(defaults.email),
                ) { it }
            )
            existSequence = userModel.id
        }

        @DisplayName("존재하지 않는 ID의 경우에 실패한다.")
        @Test
        fun getUserFailureTest() {
            assertThatThrownBy { userService.getUserModel(2L) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("유저의 아이디가 존재하지 않습니다.")
        }

        @DisplayName("존재하는 유저의 ID의 경우에는 유저를 반환한다")
        @Test
        fun getUserSuccessTest() {
            // when
            val userModel = userService.getUserModel(existSequence)

            // then
            assertNotNull(userModel)
            assertEquals(existSequence, userModel.id)
        }
    }
}
