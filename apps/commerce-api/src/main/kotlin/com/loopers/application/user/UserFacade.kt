package com.loopers.application.user

import com.loopers.domain.user.UserModel
import com.loopers.domain.user.UserService
import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class UserFacade(
    private val userService: UserService,
    private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {

    fun createUser(
        loginId: String,
        name: String,
        birth: LocalDate,
        password: String,
        email: String,
    ): UserInfo {
        val birthVO = BirthVO(birth)
        val emailVO = EmailVO(email)

        userService.checkLoginIdDuplication(loginId)
        val userModel = userService.createUserModel(
            loginId, name, password, birthVO, emailVO,
            encoder = bCryptPasswordEncoder::encode,
        )
        return UserInfo.from(userModel)
    }

    fun getUserInfo(id: Long): UserInfo {
        val userModel: UserModel = userService.getUserModel(id)
        return UserInfo.from(userModel)
    }

    fun changePassword(id: Long, oldPassword: String, targetPassword: String) {
        val userModel: UserModel = userService.getUserModel(id)
        userModel.validPasswordChange(oldPassword, targetPassword, bCryptPasswordEncoder::matches)

        val encrypted = bCryptPasswordEncoder.encode(targetPassword)
        userService.changePassword(userModel, encrypted)
    }
}
