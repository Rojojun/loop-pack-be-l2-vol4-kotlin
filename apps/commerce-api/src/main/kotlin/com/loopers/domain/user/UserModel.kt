package com.loopers.domain.user

import com.loopers.domain.BaseEntity
import com.loopers.domain.value.BirthVO
import com.loopers.domain.value.EmailVO
import com.loopers.support.security.PasswordEncoder
import com.loopers.support.security.PasswordMatcher
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.Transient
import java.util.regex.Pattern

@Entity
@Table(name = "users")
class UserModel(
    login: String,
    name: String,
    password: String,
    birth: BirthVO,
    email: EmailVO,
) : BaseEntity() {
    var loginId: String = login
        protected set
    var name: String = name
        protected set
    var password: String = password
        protected set
    @field:Embedded
    var birth: BirthVO = birth
        protected set
    @field:Embedded
    var email: EmailVO = email
        protected set

    fun changePassword(encrypted: String?) {
        this.password = encrypted ?: throw IllegalArgumentException()
    }

    fun validPasswordChange(oldPassword: String, targetPassword: String, matcher: PasswordMatcher) {
        require(matcher.matches(oldPassword, this.password)) { "비밀번호가 일치하지 않습니다." }
        require(!matcher.matches(targetPassword, this.password)) { "현재 비밀번호는 사용할 수 없습니다." }
        validatePassword(targetPassword, this.birth)
    }

    companion object {
        @Transient
        private val PATTERN: Pattern = Pattern.compile("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]{8,16}$")

        fun of(loginId: String, name: String, password: String, birth: BirthVO, email: EmailVO, encoder: PasswordEncoder): UserModel {
            validatePassword(password, birth)
            val encoded = encoder.encode(password)
            return UserModel(loginId, name, encoded, birth, email)
        }

        private fun validatePassword(rawPassword: String, birthVO: BirthVO) {
            require(PATTERN.matcher(rawPassword).matches()) { "비밀번호 생성 규칙 위반 : 8 ~ 16자의 영문 대소문자, 숫자, 특수문자만 가능합니다" }
            require(!rawPassword.contains(birthVO.toString())) { "비밀번호 생성 규칙 위반 : 생년월일은 비밀번호 내에 포함할 수 없습니다." }
        }
    }
}
