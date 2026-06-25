package com.loopers.domain.user

import com.loopers.support.error.ErrorType
import com.loopers.support.specification.Spec

class UserIsSelf(private val requestId: Long) : Spec<UserModel>(
    errorMessage = "본인만 접근할 수 있습니다.",
    errorType = ErrorType.FORBIDDEN,
) {
    override fun isSatisfiedBy(candidate: UserModel): Boolean = candidate.id == requestId
}