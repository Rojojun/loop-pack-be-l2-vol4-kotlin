package com.loopers.application.brand

import com.loopers.domain.brand.BrandStatus

data class AdminBrandInfo(
    val id: Long,
    val name: String,
    val status: BrandStatus,
)
