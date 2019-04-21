package ru.appkode.base.entities.core.movies.details

import movies.details.MovieDetailsNM
import ru.appkode.base.entities.core.books.lists.BookListItemUM

fun MovieDetailsNM.toUiModel(): MovieDetailsUM {
    return MovieDetailsUM(
        id = -id,
        title = title,
        averageRating = vote_average,
        backdrop = backdrop_path,
        poster = poster_path,
        cast = credits?.cast,
        genres = genres,
        overview = overview,
        release_date = release_date,
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