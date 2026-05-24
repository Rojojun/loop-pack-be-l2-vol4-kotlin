package com.loopers.fixture

import com.loopers.domain.user.UserModel
import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.support.security.PasswordEncoder
import java.time.LocalDate

data class UserModelFixture(
    val loginId: String,
    val name: String,
    val password: String,
    val birth: LocalDate,
    val email: String,
) {
    fun toModel(encoder: PasswordEncoder = PasswordEncoder { it }): UserModel =
        UserModel.of(loginId, name, password, BirthVO(birth), EmailVO(email), encoder)

    companion object {
        fun defaults(): UserModelFixture = UserModelFixture(
            loginId = "testId",
            name = "테스터",
            password = "test_1234",
            birth = LocalDate.of(1993, 3, 16),
            email = "test@test.com",
        )

        fun duplicate(): UserModelFixture = UserModelFixture(
            loginId = "duplicate",
            name = "테스터",
            password = "test_1234",
            birth = LocalDate.of(1993, 3, 16),
            email = "test@test.com",
        )

        fun custom(
            loginId: String,
            name: String,
            password: String,
            birth: LocalDate,
            email: String,
        ): UserModelFixture = UserModelFixture(loginId, name, password, birth, email)
    }
}
