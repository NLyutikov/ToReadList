package movies.details

data class MovieDetailsNM(
    val adult: Boolean?,
    val backdrop_path: String?,
    val belongs_to_collection: Any?,
    val budget: Int?,
    val credits: Credits?,
    val genres: List<Genre>?,
    val homepage: String?,
    val id: Long,
    val imdb_id: String?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val production_companies: List<ProductionCompany?>?,
    val production_countries: List<ProductionCountry?>?,
    val release_date: String?,
    val revenue: Int?,
    val runtime: Int?,
    val spoken_languages: List<SpokenLanguage?>?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    val video: Boolean?,
    val videos: Videos?,
    val vote_average: Double?,
    val vote_count: Int?
)

data class Credits(
    val cast: List<Cast>?,
    val crew: List<Crew>?
)

data class Cast(
    val cast_id: Int?,
    val character: String?,
    val credit_id: String?,
    val gender: Int?,
    val id: Int?,
    val name: String?,
    val order: Int?,
    val profile_path: String?
)

data class Crew(
    val credit_id: String?,
    val department: String?,
    val gender: Int?,
    val id: Int?,
    val job: String?,
    val name: String?,
    val profile_path: String?
)

data class Genre(
    val id: Int?,
    val name: String?
)

data class ProductionCompany(
    val id: Int?,
    val logo_path: Any?,
    val name: String?,
    val origin_country: String?
)

data class ProductionCountry(
    val iso_3166_1: String?,
    val name: String?
)

data class VideoResult(
    val id: String?,
    val iso_3166_1: String?,
    val iso_639_1: String?,
    val key: String?,
    val name: String?,
    val site: String?,
    val size: Int?,
    val type: String?
)

data class Videos(
    val results: List<VideoResult>?
)

data class SpokenLanguage(
    val iso_639_1: String?,
    val name: String?
)