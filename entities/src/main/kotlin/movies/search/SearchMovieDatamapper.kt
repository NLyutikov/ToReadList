package ru.appkode.base.entities.core.movies.search

import movies.search.SearchMovieNM
import movies.search.SearchMovieResult
import ru.appkode.base.entities.core.books.lists.BookListItemUM

fun SearchMovieNM.toListOfBookListUM(): List<BookListItemUM> {
    val result = results
    return result?.map { it.toBookListUM() } ?: emptyList()
}

fun SearchMovieResult.toBookListUM(): BookListItemUM {
    return BookListItemUM(
        id = -id,
        averageRating = vote_average,
        title = title,
        imagePath = poster_path
    )
}