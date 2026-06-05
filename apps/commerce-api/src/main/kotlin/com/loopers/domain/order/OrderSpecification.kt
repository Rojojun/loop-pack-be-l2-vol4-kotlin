package com.loopers.domain.order

import com.loopers.support.error.ErrorType
import com.loopers.support.specification.Spec

object OrderItemsNotEmpty : Spec<List<OrderItemModel>>(
    errorMessage = "주문 항목은 1개 이상이어야 합니다.",
) {
    override fun isSatisfiedBy(candidate: List<OrderItemModel>): Boolean = candidate.isNotEmpty()
}

object OrderIsCancellable : Spec<OrderModel>(
    errorMessage = "PENDING 상태의 주문만 취소할 수 있습니다."
) {
    override fun isSatisfiedBy(candidate: OrderModel): Boolean = candidate.status == OrderStatus.PENDING
}

class OrderOwnedBy(private val requestId: Long) : Spec<OrderModel>(
    errorMessage = "본인의 주문만 접근할 수 있습니다.",
    errorType = ErrorType.FORBIDDEN
) {
    override fun isSatisfiedBy(candidate: OrderModel): Boolean = candidate.userId == requestId
}
