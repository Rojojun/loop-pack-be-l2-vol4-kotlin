package com.loopers.support.specification

import com.loopers.support.error.ErrorType

abstract class Spec<T> (
    val errorMessage: String,
    val errorType: ErrorType = ErrorType.BAD_REQUEST,
) {
    abstract fun isSatisfiedBy(candidate: T): Boolean
}
