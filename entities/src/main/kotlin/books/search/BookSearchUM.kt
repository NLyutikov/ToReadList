package ru.appkode.base.entities.core.books.search

data class BookSearchUM(
    val book: List<BookUM>?
)

data class BookUM(
    val id: Long?,
    val title: String? = null,
    val imgPath: String? = null,
    val averageRating: Double? = null,
    val isInWish: Boolean? = false,
    val isInHistory: Boolean? = false
)
