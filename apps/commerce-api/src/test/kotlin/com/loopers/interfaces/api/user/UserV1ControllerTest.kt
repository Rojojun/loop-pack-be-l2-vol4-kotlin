package com.loopers.interfaces.api.user

import com.loopers.domain.user.UserModel
import com.loopers.fixture.UserModelFixture
import com.loopers.infrastructure.user.UserJpaRepository
import com.loopers.interfaces.api.ApiResponse
import com.loopers.utils.DatabaseCleanUp
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDate
import java.util.stream.Stream

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserV1ControllerTest @Autowired constructor (
    private val testRestTemplate: TestRestTemplate,
    private val userJpaRepository: UserJpaRepository,
    private val databaseCleanUp: DatabaseCleanUp,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    private val requestBaseUrl = "/api/v1/users"

    @AfterEach
    fun tearDown() {
        databaseCleanUp.truncateAllTables()
    }

    private fun jsonHeaders(): HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
    }

    @DisplayName("GET /api/v1/users/{id}")
    @Nested
    internal inner class Get {
        lateinit var userModel: UserModel

        @BeforeEach
        fun init() {
            val target = UserModelFixture.defaults().toModel()
            target.changePassword(passwordEncoder.encode(target.password))
            userModel = userJpaRepository.save(target)
        }

        @DisplayName("존재하는 유저 ID를 주면, 해당 유저 정보를 반환한다.")
        @Test
        fun returnsUsers_whenValidIdIsProvided() {
            // given
            val requestUrl = "$requestBaseUrl/${userModel.id}"

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response: ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(null), responseType)

            // then
            assertAll(
                { assertTrue(response.statusCode.is2xxSuccessful) },
                { assertThat(response.body).isNotNull },
                { assertThat(response.body!!.data!!.loginId).isEqualTo(userModel.loginId) },
            )
        }

        @DisplayName("존재하지 않는 유저 ID를 주면, 404 NOT_FOUND응답을 받는다")
        @Test
        fun throwsUsers_whenInValidIdIsProvided() {
            // given
            val requestUrl = "$requestBaseUrl/999999"

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response: ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(null), responseType)

            // then
            assertAll(
                { assertTrue(response.statusCode.is4xxClientError) },
                { assertThat<HttpStatusCode>(response.statusCode).isEqualTo(HttpStatus.NOT_FOUND) },
            )
        }

        @DisplayName("숫자가 아닌 유저 ID를 주면, 400 BAD_REQUEST응답을 받는다")
        @Test
        fun throwsBadRequest_whenInValidIdIsProvided() {
            // given
            val requestUrl = "$requestBaseUrl/999_999"

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response: ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> =
                testRestTemplate.exchange(requestUrl, HttpMethod.GET, HttpEntity<Any>(null), responseType)

            // then
            assertAll(
                { assertTrue(response.statusCode.is4xxClientError) },
                { assertThat<HttpStatusCode>(response.statusCode).isEqualTo(HttpStatus.BAD_REQUEST) },
            )
        }
    }

    @DisplayName("POST /api/v1/users")
    @Nested
    internal inner class Post {
        @DisplayName("유효한 입력값이 들어오면, 해당 유저 정보를 반환한다.")
        @Test
        fun returnUserResponse_whenValidInputProvided() {
            // given
            val request = UserV1Dto.CreateUserRequest(
                "tester",
                "테스터",
                LocalDate.of(1993, 3, 16),
                "q1w2e3r4!",
                "test@test.com",
            )
            val httpEntity = HttpEntity(request, jsonHeaders())

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response: ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> =
                testRestTemplate.exchange(requestBaseUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertAll(
                { assertEquals(HttpStatus.OK, response.statusCode) },
                { assertNotNull(response.body) },
                { assertEquals("tester", response.body!!.data!!.loginId) },
            )
        }

        @DisplayName("유효하지 않은 입력값이 들어오면, 400 Bad Request를 반환한다.")
        @ParameterizedTest(name = "{1}")
        @MethodSource("com.loopers.interfaces.api.user.UserV1ControllerTest#invalidInputsForPost")
        fun throwsBadRequest_whenInvalidInputProvided(
            request: UserV1Dto.CreateUserRequest,
            description: String,
        ) {
            // given
            val httpEntity = HttpEntity(request, jsonHeaders())

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<UserV1Dto.UserResponse>>() {}
            val response: ResponseEntity<ApiResponse<UserV1Dto.UserResponse>> =
                testRestTemplate.exchange(requestBaseUrl, HttpMethod.POST, httpEntity, responseType)

            // then
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }
    }

    @DisplayName("PATCH /api/v1/users/{id}")
    @Nested
    internal inner class Patch {
        var id: Long = 0L

        @BeforeEach
        fun init() {
            val target = UserModelFixture.defaults().toModel()
            target.changePassword(passwordEncoder.encode(target.password))
            id = userJpaRepository.save(target).id
        }

        @DisplayName("유효한 입력값이 들어오면, OK에 body는 null 인 상태를 반환한다.")
        @Test
        fun returnNon_whenValidInputProvided() {
            // given
            val request = UserV1Dto.ChangeUserPasswordRequest(
                UserModelFixture.defaults().password,
                "target_1234",
            )
            val httpEntity = HttpEntity(request, jsonHeaders())

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<Any>>() {}
            val response: ResponseEntity<ApiResponse<Any>> =
                testRestTemplate.exchange("$requestBaseUrl/$id", HttpMethod.PATCH, httpEntity, responseType)

            // then
            assertEquals(HttpStatus.OK, response.statusCode)
        }

        @DisplayName("유효하지 않은 입력값이 들어오면, 400 Bad Request를 반환한다.")
        @ParameterizedTest(name = "{1}")
        @MethodSource("com.loopers.interfaces.api.user.UserV1ControllerTest#invalidInputsForPatch")
        fun throwsBadRequest_whenInvalidInputProvided(
            request: UserV1Dto.ChangeUserPasswordRequest,
            description: String,
        ) {
            // given
            val httpEntity = HttpEntity(request, jsonHeaders())

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<Any>>() {}
            val response: ResponseEntity<ApiResponse<Any>> =
                testRestTemplate.exchange("$requestBaseUrl/$id", HttpMethod.PATCH, httpEntity, responseType)

            // then
            assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
        }

        @DisplayName("존재하지 않는 유저 ID를 주면, 404 NOT_FOUND를 호출한다.")
        @Test
        fun throwsBadRequest_whenIdEmpty() {
            // given
            val invalidId = 999999L
            val request = UserV1Dto.ChangeUserPasswordRequest("original_1234", "target_1234")
            val httpEntity = HttpEntity(request, jsonHeaders())

            // when
            val responseType = object : ParameterizedTypeReference<ApiResponse<Any>>() {}
            val response: ResponseEntity<ApiResponse<Any>> =
                testRestTemplate.exchange("$requestBaseUrl/$invalidId", HttpMethod.PATCH, httpEntity, responseType)

            // then
            assertEquals(HttpStatus.NOT_FOUND, response.statusCode)
        }
    }

    companion object {
        @JvmStatic
        fun invalidInputsForPost(): Stream<Arguments> {
            val validBirth = LocalDate.of(1993, 3, 16)
            return Stream.of(
                of(UserV1Dto.CreateUserRequest("", "테스터", validBirth, "q1w2e3r4!", "test@test.com"), "loginId 공백"),
                of(UserV1Dto.CreateUserRequest("tester", "", validBirth, "q1w2e3r4!", "test@test.com"), "name 공백"),
                of(UserV1Dto.CreateUserRequest("tester", "테스터", validBirth, "short", "test@test.com"), "비밀번호 8자 미만"),
                of(UserV1Dto.CreateUserRequest("tester", "테스터", validBirth, "q1w2e3r4!@#$%^&*(", "test@test.com"), "비밀번호 16자 초과"),
                of(UserV1Dto.CreateUserRequest("tester", "테스터", validBirth, "q1w2e3r4!", "not-email"), "이메일 형식 오류"),
            )
        }

        @JvmStatic
        fun invalidInputsForPatch(): Stream<Arguments> = Stream.of(
            of(UserV1Dto.ChangeUserPasswordRequest("orig_12", "target_1234"), "oldPassword 8자 미만"),
            of(UserV1Dto.ChangeUserPasswordRequest("original_1234", "targ_12"), "targetPassword 8자 미만"),
            of(UserV1Dto.ChangeUserPasswordRequest("original_12345678", "target_1234"), "oldPassword 16자 초과"),
            of(UserV1Dto.ChangeUserPasswordRequest("original_1234", "target_1234567890"), "targetPassword 16자 초과"),
        )
    }
}
