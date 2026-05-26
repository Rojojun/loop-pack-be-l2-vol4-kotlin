package com.loopers.domain.brand

import com.loopers.domain.BaseEntity
import com.loopers.domain.order.OrderModel
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "brands")
class BrandModel private constructor (
    name: String,
    status: BrandStatus
) : BaseEntity() {
    var name: String = name
        protected set

    @Enumerated(EnumType.STRING)
    var status: BrandStatus = status
        protected set

    fun update(name: String) {
        this.name = name
    }

    fun statusChange(status: BrandStatus) {
        this.status = status
    }

    companion object {
        fun of(name: String): BrandModel =
            BrandModel(name, BrandStatus.ACTIVE)
    }
}
