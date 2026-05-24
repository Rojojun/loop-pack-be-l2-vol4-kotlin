package com.loopers.interfaces.api.user

import com.loopers.application.user.UserInfo
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import jakarta.validation.constraints.Pattern
import java.time.LocalDate

class UserV1Dto {
    data class CreateUserRequest(
        @field:NotBlank
        val loginId: String,
        @field:NotBlank
        val name: String,
        @field:NotNull
        @field:Past
        val birth: LocalDate,
        @field:NotBlank
        @field:Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,16}$",
            message = "비밀번호 생성 규칙 위반 : 8 ~ 16자의 영문 대소문자, 숫자, 특수문자만 가능합니다",
        )
        val password: String,
        @field:NotBlank
        @field:Email
        val email: String,
    )

    data class ChangeUserPasswordRequest(
        @field:NotBlank
        @field:Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,16}$",
            message = "비밀번호 생성 규칙 위반 : 8 ~ 16자의 영문 대소문자, 숫자, 특수문자만 가능합니다",
        )
        val oldPassword: String,
        @field:NotBlank
        @field:Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,16}$",
            message = "비밀번호 생성 규칙 위반 : 8 ~ 16자의 영문 대소문자, 숫자, 특수문자만 가능합니다",
        )
        val targetPassword: String,
    )

    data class UserResponse(
        val loginId: String,
        val name: String,
        val birth: LocalDate,
        val email: String
    ) {
        companion object {
            fun from(info: UserInfo): UserResponse {
                return UserResponse(
                    info.loginId,
                    info.name,
                    info.birth.date,
                    info.email.email,
                )
            }
        }
    }
}
