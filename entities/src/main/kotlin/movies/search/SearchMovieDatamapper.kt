package ru.appkode.base.entities.core.movies.search

import movies.search.SearchMovieNM
import movies.search.SearchMovieResult
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.movies.BASE_MOVIE_IMAGE_URL
import ru.appkode.base.entities.core.movies.POSTER_SIZE

fun SearchMovieNM.toListOfBookListUM(): List<BookListItemUM> {
    val result = results
    return result?.map { it.toBookListUM() } ?: emptyList()
}

fun SearchMovieResult.toBookListUM(): BookListItemUM {
    return BookListItemUM(
        id = -id,
        averageRating = vote_average,
        title = title,
        imagePath = BASE_MOVIE_IMAGE_URL + POSTER_SIZE + poster_path
    )
}