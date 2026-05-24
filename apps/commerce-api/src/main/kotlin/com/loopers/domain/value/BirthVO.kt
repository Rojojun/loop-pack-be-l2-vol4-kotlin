package com.loopers.domain.value

import jakarta.persistence.Embeddable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Embeddable
data class BirthVO(
    val date: LocalDate
) {
    override fun toString(): String {
        return this.date.format(DateTimeFormatter.BASIC_ISO_DATE)
    }
}
