package ru.appkode.base.entities.core.movies.details

import movies.details.Cast
import movies.details.Genre
import movies.details.VideoResult
import movies.details.Videos

data class MovieDetailsUM (
    val id: Long,
    val title: String?,
    val averageRating: Double?,
    val release_date: String?,
    val runtime: Int?,
    val genres: List<Genre>?,
    val poster: String?,
    val backdrop: String?,
    val overview: String?,
    val cast: List<Cast>?,
    val videos: List<VideoResult>?,
    val isInWishList: Boolean = false,
    val isInHistory: Boolean = false
)