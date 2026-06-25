package com.loopers.infrastructure.user

import com.loopers.domain.user.UserModel
import com.loopers.domain.user.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository
) : UserRepository {
    override fun save(userModel: UserModel): UserModel {
        return userJpaRepository.save<UserModel>(userModel)
    }

    override fun findByLoginId(loginId: String): java.util.Optional<UserModel> {
        return userJpaRepository.findByLoginId(loginId)
    }

    override fun findByIdOrNull(id: Long): UserModel? {
        return userJpaRepository.findByIdOrNull(id)
    }

    override fun existsByLoginId(loginId: String): Boolean {
        return userJpaRepository.existsByLoginId(loginId)
    }
}
