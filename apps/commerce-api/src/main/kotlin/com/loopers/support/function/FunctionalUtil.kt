package com.loopers.support.function

import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import com.loopers.support.specification.Spec

infix fun <T: Any> T?.orThrowNotFound(message: String): T =
    this ?: throw CoreException(ErrorType.NOT_FOUND, message)

infix fun <T> T.ensure(rule: Spec<T>): T =
    also { if (!rule.isSatisfiedBy(it)) throw CoreException(rule.errorType, rule.errorMessage) }
