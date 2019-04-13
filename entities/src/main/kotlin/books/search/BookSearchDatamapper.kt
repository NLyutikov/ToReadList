package ru.appkode.base.entities.core.books.search

fun BookSearchNM.toUiModel(): List<SearchResultUM> {
    return this.work.orEmpty().map { it.toUiModel() }
}

fun SearchResultNM.toUiModel(): SearchResultUM {
    return SearchResultUM(
        id,
        title,
        image_url,
        average_rating,
        isInWish = false,
        isInHistory = false
    )
}
