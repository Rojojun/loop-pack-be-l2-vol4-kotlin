package com.loopers.infrastructure.user

import com.loopers.domain.user.UserModel
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserJpaRepository : JpaRepository<UserModel, Long> {
    fun findByLoginId(loginId: String): Optional<UserModel>

    fun existsByLoginId(loginId: String): Boolean
}
