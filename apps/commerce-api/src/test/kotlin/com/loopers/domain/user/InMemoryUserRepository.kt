package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import java.util.Optional

class InMemoryUserRepository : UserRepository {
    private val data: MutableMap<Long, UserModel> = HashMap()
    private var sequence = 1L

    override fun save(userModel: UserModel): UserModel {
        val id = sequence++
        try {
            val idField = BaseEntity::class.java.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(userModel, id)
            idField.isAccessible = false
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

        data[id] = userModel
        return userModel
    }

    override fun findByLoginId(loginId: String): Optional<UserModel> =
        data.values.stream()
            .filter { it.loginId == loginId }
            .findFirst()

    override fun findByIdOrNull(id: Long): UserModel? = data[id]

    override fun existsByLoginId(loginId: String): Boolean =
        data.values.any { it.loginId == loginId }
}
