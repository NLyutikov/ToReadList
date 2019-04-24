package ru.appkode.base.entities.core.movies.details

import movies.details.MovieDetailsNM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.movies.BACKDROP_SIZE
import ru.appkode.base.entities.core.movies.BASE_MOVIE_IMAGE_URL
import ru.appkode.base.entities.core.movies.POSTER_SIZE

fun MovieDetailsNM.toUiModel(): MovieDetailsUM {
    return MovieDetailsUM(
        id = -id,
        title = title,
        averageRating = vote_average,
        backdrop = BASE_MOVIE_IMAGE_URL + BACKDROP_SIZE + backdrop_path,
        poster = BASE_MOVIE_IMAGE_URL + POSTER_SIZE + poster_path,
        cast = credits?.cast,
        genres = genres,
        overview = overview,
        releaseDate = release_date,
        runtime = runtime,
        videos = videos?.results
    )
}

fun MovieDetailsUM.toBookListUM(): BookListItemUM {
    return BookListItemUM(
        id = id,
        title = title,
        averageRating = averageRating,
        imagePath = poster,
        isInWishList = isInWishList,
        isInHistory = isInHistory
    )
}