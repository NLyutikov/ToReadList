package ru.appkode.base.entities.core.books.search

data class BookSearchUM(
    val original_publication_year: Int? = null,
    val original_publication_month: Int? = null,
    val original_publication_day: Int? = null,
    val average_rating: String? = null,
    val best_book: List<BestBookUM>? = null
)

data class BestBookUM(
    val id: Int?,
    val title: String? = null,
    val author: List<AuthorUM>? = null,
    val img_url: String? = null,
    val small_img_url: String? = null
)

data class AuthorUM(
    val id: Int,
    val name: String? = null
)
