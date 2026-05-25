package com.loopers.domain.order

import com.loopers.support.specification.Spec

object OrderItemsNotEmpty : Spec<List<OrderItemModel>>(
    errorMessage = "주문 항목은 1개 이상이어야 합니다.",
) {
    override fun isSatisfiedBy(candidate: List<OrderItemModel>): Boolean = candidate.isNotEmpty()
}
