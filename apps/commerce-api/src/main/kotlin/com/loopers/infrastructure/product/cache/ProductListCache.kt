package com.loopers.infrastructure.product.cache

interface ProductListCache {

    /**
     * 정렬된 id 배열에서 [offset, offset+limit) 구간을 반환한다.
     * @return 미스(키 없음)면 null — 호출부가 rebuild 를 트리거한다.
     */
    fun getIds(sort: String, offset: Long, limit: Long): List<Long>?

    /** 캐시된 리스트 전체 길이 (= COUNT, ADR-0002 D2). 페이지 total 에 쓴다. */
    fun size(sort: String): Long

    /** 정렬된 전체 id(상위 N) 로 리스트를 통째 재구성한다 (DEL + 적재 + TTL). */
    fun rebuild(sort: String, ids: List<Long>)

    /** 새 상품을 head 에 끼운다 (최신순 LPUSH + LTRIM, D5-b). */
    fun pushHead(sort: String, productId: Long)

    /** 삭제/status 변경 시 리스트에서 제거한다 (LREM, D5-b). */
    fun remove(sort: String, productId: Long)
}
