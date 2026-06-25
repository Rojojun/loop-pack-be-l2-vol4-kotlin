package com.loopers.domain.value

import jakarta.persistence.Embeddable
import java.util.regex.Pattern

@Embeddable
data class EmailVO(
    val email: String
) {
    init {
        require(PATTERN.matcher(email).matches()) { "올바르지 않는 이메일 형식 입니다." }
    }

    companion object {
        private val PATTERN: Pattern = Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")
    }
}
