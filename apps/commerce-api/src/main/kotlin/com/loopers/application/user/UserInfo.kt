package com.loopers.application.user

import com.loopers.domain.user.UserModel
import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO

data class UserInfo(
    val loginId: String,
    val name: String,
    val birth: BirthVO,
    val email: EmailVO,
) {
    companion object {
        fun from(userModel: UserModel): UserInfo {
            val originalName = userModel.name
            val masking = originalName.substring(0, originalName.length - 1) + "*"

            return UserInfo(
                loginId = userModel.loginId,
                name = masking,
                birth = userModel.birth,
                email = userModel.email,
            )
        }
    }
}
