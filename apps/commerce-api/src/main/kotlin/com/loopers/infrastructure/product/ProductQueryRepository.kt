package com.loopers.infrastructure.product

import com.loopers.domain.like.QProductLikeCountModel.productLikeCountModel
import com.loopers.domain.product.Level
import com.loopers.domain.product.ProductModel
import com.loopers.domain.product.QProductModel.productModel
import com.loopers.domain.product.TechCategory
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Component

@Component
class ProductQueryRepository(
    private val jpaQueryFactory: JPAQueryFactory
) {
    fun findProducts(brandId: Long?, pageable: Pageable): Page<ProductModel> {
        val condition = isBrandEq(brandId)

        val content = basePagingQuery(pageable, condition).fetch()
        val countQuery = baseCountingQuery(condition)

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    fun findProducts(brandId: Long?, category: TechCategory?, level: Level?, sort: String, pageable: Pageable): Page<ProductModel> {
        val conditions = arrayOf(
            isBrandEq(brandId),
            isCategoryEq(category),
            isLevelEq(level)
        )

        val content = when (sort) {
            "likes_desc" -> jpaQueryFactory.selectFrom(productModel)
                .leftJoin(productLikeCountModel).on(
                    productModel.id.eq(productLikeCountModel.productId),
                )
                .where(*conditions)
                .orderBy(productLikeCountModel.likeCount.desc(), productModel.id.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetch()

            "price_asc" -> basePagingQuery(pageable, *conditions)
                .orderBy(productModel.price.asc(), productModel.id.desc())
                .fetch()

            else -> basePagingQuery(pageable, *conditions)
                .orderBy(productModel.createdAt.desc())
                .fetch()
        }
        val countQuery = baseCountingQuery(*conditions)

        return PageableExecutionUtils.getPage(content, pageable) { countQuery.fetchOne() ?: 0L }
    }

    private fun basePagingQuery(pageable: Pageable, vararg conditions: BooleanExpression?) =
        jpaQueryFactory.selectFrom(productModel)
            .offset(pageable.offset)
            .where(*conditions)
            .limit(pageable.pageSize.toLong())

    private fun baseCountingQuery(vararg conditions: BooleanExpression?) =
        jpaQueryFactory
            .select(productModel.count())
            .from(productModel)
            .where(*conditions)

    private fun isBrandEq(brandId: Long?): BooleanExpression? =
        brandId?.let { productModel.brandId.eq(it) }

    private fun isCategoryEq(category: TechCategory?): BooleanExpression? =
        category?.let { productModel.techCategory.eq(it) }

    private fun isLevelEq(level: Level?): BooleanExpression? =
        level?.let { productModel.level.eq(it) }
}
