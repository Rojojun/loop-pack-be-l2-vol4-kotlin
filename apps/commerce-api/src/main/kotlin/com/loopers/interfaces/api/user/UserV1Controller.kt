package com.loopers.interfaces.api.user

import com.loopers.application.user.UserFacade
import com.loopers.application.user.UserInfo
import com.loopers.interfaces.api.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserV1Controller(
    private val userFacade: UserFacade
) : UserV1ApiSpec {
    @PostMapping
    override fun createUser(
        @RequestBody @Valid request: UserV1Dto.CreateUserRequest
    ): ApiResponse<UserV1Dto.UserResponse> {
        val info: UserInfo = userFacade.createUser(
            request.loginId,
            request.name,
            request.birth,
            request.password,
            request.email
        )

        val response: UserV1Dto.UserResponse =
            UserV1Dto.UserResponse.from(info)

        return ApiResponse.success(response)
    }

    @PatchMapping("/{id}")
    override fun updateUserPassword(
        @PathVariable id: Long,
        @RequestBody @Valid request: UserV1Dto.ChangeUserPasswordRequest
    ): ApiResponse<Any> {
        userFacade.changePassword(id, request.oldPassword, request.targetPassword)

        return ApiResponse.success()
    }

    @GetMapping("/{id}")
    override fun getUserResponse(@PathVariable id: Long): ApiResponse<UserV1Dto.UserResponse> {
        val userInfo: UserInfo = userFacade.getUserInfo(id)

        val response: UserV1Dto.UserResponse =
            UserV1Dto.UserResponse.from(userInfo)

        return ApiResponse.success(response)
    }
}
