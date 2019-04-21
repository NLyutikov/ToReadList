package movies.search

data class SearchMovieNM(
    val page: Int,
    val results: List<SearchMovieResult>?,
    val total_pages: Int,
    val total_results: Int
)

data class SearchMovieResult(
    val adult: Boolean,
    val backdrop_path: String?,
    val genre_ids: List<Int>?,
    val id: Long,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String?,
    val release_date: String,
    val title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)