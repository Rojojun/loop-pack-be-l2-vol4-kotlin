package com.loopers.domain.user

import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.fixture.UserModelFixture
import com.loopers.support.security.PasswordMatcher
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.Month
import java.util.stream.Stream

class UserModelTest {
    @DisplayName("유저 모델을 생성할 때")
    @Nested
    internal inner class Create {
        @DisplayName("유효한 유저의 경우에는 성공한다.")
        @MethodSource("com.loopers.domain.user.UserModelTest#signupSuccessTestParams")
        @ParameterizedTest
        fun userSuccessTest(password: String?, email: String) {
            // given
            val loginId = "tester"
            val name = "tester"
            val birth = BirthVO(LocalDate.of(1993, Month.MARCH, 16))
            val emailVO = EmailVO(email)

            // when
            val userModel = UserModel.of(loginId, name, password!!, birth, emailVO) { it }

            // then
            org.junit.jupiter.api.Assertions.assertEquals(loginId, userModel.loginId)
        }

        @DisplayName("생년월일이 비밀번호에 포함되어 있는 경우 실패한다")
        @Test
        fun userFailTest() {
            // given
            val loginId = "tester"
            val name = "tester"
            val birth = BirthVO(LocalDate.of(1993, Month.MARCH, 16))
            val password = "19930316_pwd"
            val emailVO = EmailVO("test@test.com")

            // when then
            org.assertj.core.api.Assertions.assertThatThrownBy {
                UserModel.of(loginId, name, password, birth, emailVO) { it }
            }
                .isInstanceOf(java.lang.IllegalArgumentException::class.java)
                .hasMessage("비밀번호 생성 규칙 위반 : 생년월일은 비밀번호 내에 포함할 수 없습니다.")
        }

    }

    @DisplayName("유저 비밀번호 변경 유효성 테스트")
    @MethodSource("validPasswordChangeTestParams")
    @ParameterizedTest(name = "{0}")
    fun validPasswordChangeTest(
        description: String,
        originalPassword: String,
        targetPassword: String,
        exceptionWord: String
    ) {
        // given
        val userModel: UserModel = UserModelFixture.defaults().toModel()
        val passwordMatcher = PasswordMatcher { raw, encoded -> raw == encoded }

        // when then
        org.assertj.core.api.Assertions.assertThatThrownBy {
            userModel.validPasswordChange(originalPassword, targetPassword, passwordMatcher)
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage(exceptionWord)
    }

    companion object {
        @JvmStatic
        private fun signupSuccessTestParams(): Stream<Arguments> = Stream.of(
            Arguments.of("test_1234", "test@test.com"),
            Arguments.of("test_1234_AB", "test@test.com"),
            Arguments.of("test_19940316", "test@test.com"),
        )

        @JvmStatic
        private fun validPasswordChangeTestParams(): Stream<Arguments> {
            val originalPassword = UserModelFixture.defaults().password
            return Stream.of(
                Arguments.of(
                    "원본 비밀번호가 같지 않으면 실패한다.",
                    "$originalPassword!!",
                    "${originalPassword}__",
                    "비밀번호가 일치하지 않습니다.",
                ),
                Arguments.of(
                    "바꾸려는 비밀번호가 원본과 같으면 실패한다.",
                    originalPassword,
                    originalPassword,
                    "현재 비밀번호는 사용할 수 없습니다.",
                ),
            )
        }
    }
}
