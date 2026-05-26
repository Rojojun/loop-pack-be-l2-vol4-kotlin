package com.loopers.domain.product

import com.loopers.domain.BaseEntity
import com.loopers.support.error.CoreException
import com.loopers.support.error.ErrorType
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class ProductModel private constructor (
    brandId: Long,
    isbn: String,
    name: String,
    authName: String,
    techCategory: TechCategory,
    level: Level,
    price: Double,
    status: ProductStatus,
    description: String,
): BaseEntity() {
    var brandId: Long = brandId
        protected set

    var isbn: String = isbn
        protected set

    var name: String = name
        protected set

    var authName: String = authName
        protected set

    var techCategory: TechCategory = techCategory
        protected set

    var level: Level = level
        protected set

    var price = price
        protected set

    var status: ProductStatus = status
        protected set

    var description: String = description
        protected set

    fun changeStatus(productStatus: ProductStatus) {
        this.status = productStatus
    }

    fun update(name: String, author: String, techCategory: TechCategory, level: Level, price: Double) {
        this.name = name
        this.authName = author
        this.techCategory = techCategory
        this.level = level
        this.price = price
    }

    init {
        require(name.isNotBlank()) { throw CoreException(ErrorType.BAD_REQUEST,  "상품명은 비어있을 수 없습니다.") }
        require(description.isNotBlank()) { throw CoreException(ErrorType.BAD_REQUEST, "상품 설명은 비어있을 수 없습니다.") }
    }

    companion object {
        fun of(brandId: Long, isbn: String, name: String, authName: String, techCategory: TechCategory, level: Level, price: Double, description: String): ProductModel = ProductModel(
            brandId = brandId,
            isbn = isbn,
            name = name,
            authName = authName,
            techCategory = techCategory,
            level = level,
            price = price,
            status = ProductStatus.ACTIVE,
            description = description
        )
    }
}
