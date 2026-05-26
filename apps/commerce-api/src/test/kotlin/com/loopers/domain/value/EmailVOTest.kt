package com.loopers.domain.value

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.function.Executable

class EmailVOTest {
    @DisplayName("이메일 유효성 테스트 시나리오")
    @TestFactory
    fun emailDynamicTest(): MutableCollection<DynamicTest?> {
        return mutableListOf(
            DynamicTest.dynamicTest("올바르지 않은 이메일 유형의 경우에는 Exception이 발생한다.") {
                // given
                val invalid1 = "testemail@email"
                val invalid2 = "testemail"
                val invalid3 = "testemail.com"

                // when then
                Assertions.assertThatThrownBy { EmailVO(invalid1) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("올바르지 않는 이메일 형식 입니다.")

                Assertions.assertThatThrownBy { EmailVO(invalid2) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("올바르지 않는 이메일 형식 입니다.")
                Assertions.assertThatThrownBy { EmailVO(invalid3) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessage("올바르지 않는 이메일 형식 입니다.")
            },
            DynamicTest.dynamicTest(
                "유효한 Email 형식의 경우에는 성공한다.",
                Executable {
                    // given
                    val valid = "test@email.com"

                    // when
                    val emailVO = EmailVO(valid)

                    // then
                    assertThat(emailVO.email).isEqualTo(valid)
                }
            )
        )
    }
}
