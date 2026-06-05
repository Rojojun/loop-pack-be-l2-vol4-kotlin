package com.loopers.fixture

import com.loopers.domain.brand.BrandModel

data class BrandModelFixture(
    val name: String,
) {
    fun toModel(): BrandModel = BrandModel.of(name)

    companion object {
        fun defaults() = BrandModelFixture("기본브랜드")

        fun custom(name: String) = BrandModelFixture(name)
    }
}
