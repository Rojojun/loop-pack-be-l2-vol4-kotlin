package com.loopers.domain.user

import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.security.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Transactional
@Component
class UserService(
    val userRepository: UserRepository
) {
    fun createUserModel(
        loginId: String,
        name: String,
        rawPassword: String,
        birthVO: BirthVO,
        emailVO: EmailVO,
        encoder: PasswordEncoder,
    ): UserModel {
        val userModel: UserModel = UserModel.of(loginId, name, rawPassword, birthVO, emailVO, encoder)
        return userRepository.save(userModel)
    }

    @Transactional(readOnly = true)
    fun getUserModel(id: Long): UserModel {
        return userRepository.findByIdOrNull(id).ifNullThrow()
    }

    @Transactional(readOnly = true)
    fun checkLoginIdDuplication(loginId: String) {
        if (userRepository.existsByLoginId(loginId)) {
            throw CoreException(ErrorType.BAD_REQUEST, "이미 존재하는 유저의 아이디입니다.")
        }
    }

    fun changePassword(userModel: UserModel, encrypted: String) {
        userModel.changePassword(encrypted)
        userRepository.save(userModel)
    }

    private fun UserModel?.ifNullThrow(): UserModel =
        this ?: throw CoreException(ErrorType.NOT_FOUND, "유저의 아이디가 존재하지 않습니다.")
}
