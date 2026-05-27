package com.loopers.domain.stock

import com.loopers.support.specification.Spec

object StockQuantityNotNegative: Spec<StockModel> (
    errorMessage = "재고 수량은 0 이상이어야 합니다."
) {
    override fun isSatisfiedBy(candidate: StockModel): Boolean =
        candidate.quantity >= 0
}

object StockModelReduceAtLeastOne: Spec<StockModel> (
    errorMessage = "차감 수량은 1 이상이어야 합니다."
) {
    override fun isSatisfiedBy(candidate: StockModel): Boolean =
        candidate.quantity >= 0
}

object StockModelQuantityOverThanZero: Spec<StockModel> (
    errorMessage = "재고 수량은 0 이상이어야 합니다."
) {
    override fun isSatisfiedBy(candidate: StockModel): Boolean =
        candidate.quantity >= 0
}
