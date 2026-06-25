package com.loopers.domain.product

import org.springframework.data.domain.Pageable

/**
 * 상품 리스트 조회 (CQRS query side) — 도메인 언어. repository 와 동급 자리.
 *
 * 캐시 여부는 구현 디테일이다(도메인은 모른다). stock/like 실시간 합성은 이 밖(Facade)의 책임 — D0.
 */
interface ProductViewQuery {
    fun findViewPage(
        brandId: Long?,
        category: TechCategory?,
        level: Level?,
        sort: String,
        pageable: Pageable,
    ): ProductViewPage
}
