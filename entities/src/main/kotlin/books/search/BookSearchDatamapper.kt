package ru.appkode.base.entities.core.books.search

fun BookSearchNM.toUiModel(): List<BookUM> {
    return this.work.orEmpty().map { it.toUiModel() }
}

fun SearchResultNM.toUiModel(): BookUM {
    return BookUM(
        id,
        title,
        image_url,
        average_rating,
        isInWish = false,
        isInHistory = false
    )
}
