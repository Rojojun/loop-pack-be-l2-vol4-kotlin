package com.loopers.domain.like

sealed interface LikeResult {
    data object Liked : LikeResult
    data object AlreadyLiked : LikeResult
}
