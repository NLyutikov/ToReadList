package ru.appkode.base.entities.core.books.lists

data class BookListItemUM (
    val id: Long,
    val title: String? = null,
    val averageRating: Double? = null,
    val imagePath: String? = null,
    val order: Long? = null,
    val isInWishList: Boolean = false,
    val isInHistory: Boolean = false
)