package com.loopers.domain.user

import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.fixture.UserModelFixture
import com.loopers.support.error.CoreException
import com.loopers.support.security.PasswordMatcher
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.Month
import java.util.stream.Stream

internal class UserModelTest {
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
            assertThat(userModel.loginId).isEqualTo(loginId)
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
            assertThatThrownBy {
                UserModel.of(loginId, name, password, birth, emailVO) { it }
            }
                .isInstanceOf(CoreException::class.java)
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
        assertThatThrownBy {
            userModel.validPasswordChange(originalPassword, targetPassword, passwordMatcher)
        }
            .isInstanceOf(CoreException::class.java)
            .hasMessage(exceptionWord)
    }

    @DisplayName("본인 여부를 검증할 때")
    @Nested
    internal inner class ValidateSelf {
        @DisplayName("요청자 id 가 본인과 같으면 예외가 발생하지 않는다.")
        @Test
        fun passWhenSelf() {
            // given
            val user = UserModelFixture.defaults().toModel()

            // when then
            assertThatCode { user.validateSelf(user.id) }.doesNotThrowAnyException()
        }

        @DisplayName("요청자 id 가 본인과 다르면 FORBIDDEN CoreException 을 던진다.")
        @Test
        fun forbiddenWhenNotSelf() {
            // given
            val user = UserModelFixture.defaults().toModel()

            // when then
            assertThatThrownBy { user.validateSelf(999L) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("본인만 접근할 수 있습니다.")
        }
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
