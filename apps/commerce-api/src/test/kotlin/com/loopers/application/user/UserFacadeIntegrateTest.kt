package com.loopers.application.user

import com.loopers.domain.user.UserRepository
import com.loopers.domain.user.UserService
import com.loopers.fixture.UserModelFixture
import com.loopers.support.error.CoreException
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@SpringBootTest
internal class UserFacadeIntegrateTest @Autowired constructor(
    private val userFacade: UserFacade,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder,
    private val databaseCleanUp: DatabaseCleanUp,
) {
    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    @DisplayName("유저를 생성할 때")
    @Nested
    internal inner class Create {
        @BeforeEach
        fun init() {
            val duplicate = UserModelFixture.duplicate()
            userRepository.save(duplicate.toModel())
        }

        @DisplayName("userId에 중복이 없는 경우 성공적으로 생성된다")
        @Test
        fun userCreateSuccessTest() {
            // given
            val userModelFixture = UserModelFixture.defaults()

            // when
            val result = userFacade.createUser(
                userModelFixture.loginId,
                userModelFixture.name,
                userModelFixture.birth,
                userModelFixture.password,
                userModelFixture.email,
            )

            // then
            assertNotNull(result)
            assertEquals(userModelFixture.email, result.email.email)
        }

        @DisplayName("중복인 userId가 있는 경우 실패한다")
        @Test
        fun userCreateFailureTest() {
            // given
            val duplicate = UserModelFixture.duplicate()

            // when then
            assertThatThrownBy {
                userFacade.createUser(
                    duplicate.loginId,
                    duplicate.name,
                    duplicate.birth,
                    duplicate.password,
                    duplicate.email,
                )
            }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("이미 존재하는 유저의 아이디입니다.")
        }

        @DisplayName("비밀번호는 암호화 된다")
        @Test
        fun passwordEncryptSuccessTest() {
            // given
            val userModelFixture = UserModelFixture.defaults()

            // when
            userFacade.createUser(
                userModelFixture.loginId,
                userModelFixture.name,
                userModelFixture.birth,
                userModelFixture.password,
                userModelFixture.email,
            )

            // then
            val saved = userRepository.findByLoginId(userModelFixture.loginId).get()
            assertThat(bCryptPasswordEncoder.matches(userModelFixture.password, saved.password)).isTrue()
        }
    }

    @Nested
    @DisplayName("유저를 조회할 때")
    internal inner class Read {
        private var expectedId = 0L
        private lateinit var expectedLoginId: String

        @BeforeEach
        fun init() {
            val defaults = UserModelFixture.defaults()
            val savedUser = userRepository.save(defaults.toModel())

            expectedId = savedUser.id
            expectedLoginId = defaults.loginId
        }

        @DisplayName("유효한 id로 유저를 조회하면 UserInfo를 반환한다")
        @Test
        fun getUserInfoSuccessTest() {
            // when
            val userInfo = userFacade.getUserInfo(expectedId)

            // then
            assertThat(userInfo).isInstanceOf(UserInfo::class.java)
            assertThat(userInfo.loginId).isEqualTo(expectedLoginId)
        }

        @DisplayName("유효하지 않은 id로 유저를 조회하면")
        @Test
        fun getUserInfoFailureTest() {
            // when then
            assertThatThrownBy { userFacade.getUserInfo(999999L) }
                .isInstanceOf(CoreException::class.java)
                .hasMessage("유저의 아이디가 존재하지 않습니다.")
        }
    }

    @DisplayName("유저의 정보를 변경할 때")
    @Nested
    internal inner class Update {
        private var expectedId = 0L
        private lateinit var currentPassword: String

        @BeforeEach
        fun init() {
            val defaults = UserModelFixture.defaults()
            val encrypted = bCryptPasswordEncoder.encode(defaults.password)
            val userModel = defaults.toModel()
            userModel.changePassword(encrypted)
            val savedUser = userRepository.save(userModel)

            expectedId = savedUser.id
            currentPassword = defaults.password
        }

        @DisplayName("현재비밀번호와 변경하려는 비밀번호를 받아서 변경시킨다")
        @Test
        fun changePasswordSuccessTest() {
            // given
            val target = "test_!!34"

            // when
            userFacade.changePassword(expectedId, currentPassword, target)
            val userModel = userRepository.findByIdOrNull(expectedId)!!

            // then
            assertThat(bCryptPasswordEncoder.matches(target, userModel.password)).isTrue()
        }

        @DisplayName("파라미터로 받은 비밀번호가 규칙과 일치하지 않는다면")
        @TestFactory
        fun changePasswordFailureTest(): Collection<DynamicTest> = listOf(
            DynamicTest.dynamicTest("현재비밀번호와 파라미터로 받은 현재비밀번호가 일치하지 않으면 오류가 발생하다") {
                val wrongCurrentPassword = "$currentPassword!"
                val target = "test_!!34"
                assertThatThrownBy { userFacade.changePassword(expectedId, wrongCurrentPassword, target) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("비밀번호가 일치하지 않습니다.")
            },
            DynamicTest.dynamicTest("현재비밀번호와 파라미터로 받은 변경비밀번호가 일치하면 오류가 발생하다") {
                assertThatThrownBy { userFacade.changePassword(expectedId, currentPassword, currentPassword) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("현재 비밀번호는 사용할 수 없습니다.")
            },
        )
    }
}
