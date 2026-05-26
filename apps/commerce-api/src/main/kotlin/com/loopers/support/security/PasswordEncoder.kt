package com.loopers.support.security

fun interface PasswordEncoder {
    fun encode(raw: String): String
}