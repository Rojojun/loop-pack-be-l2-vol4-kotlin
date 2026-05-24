package com.loopers.domain.user

import java.util.*

interface UserRepository {
    fun save(userModel: UserModel): UserModel

    fun findByLoginId(loginId: String): Optional<UserModel>

    fun findByIdOrNull(id: Long): UserModel?

    fun existsByLoginId(loginId: String): Boolean
}
