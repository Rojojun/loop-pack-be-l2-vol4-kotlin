package com.loopers.support.security

fun interface PasswordMatcher {
    fun matches(raw: String?, encoded: String?): Boolean
}
