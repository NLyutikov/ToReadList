package ru.appkode.base.entities.core.books.details

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GoodreadsResponse")
data class BookDetailsNM (

    @Path("book")
    @PropertyElement(name = "id")
    val goodReadsBookId: String?,

    @Path("book")
    @PropertyElement
    val title: String?,

    @Path("book")
    @PropertyElement
    val isbn: String?,

    @Path("book")
    @PropertyElement
    val isbn13: String?,

    @Path("language_code")
    @PropertyElement
    val languageCode: String?,

    @Path("book")
    @PropertyElement(name = "image_url")
    val coverImageUrl: String?,

    @Path("book")
    @PropertyElement(name = "small_image_url")
    val smallCoverImageUrl: String?,

    @Path("book")
    @PropertyElement(name = "num_pages")
    val pagesNumber: String?,

    @Path("book")
    @PropertyElement
    val description: String?,

    @Path("book")
    @PropertyElement
    val ratingsCount: String?,

    @Path("book/series_works")
    @Element
    val seriesWorks: List<SeriesWorkNM>?,

    @Path("book/authors")
    @Element
    val authors: List<AuthorNM>?,

    @Path("book/similar_books")
    @Element
    val similarBooks: List<ShortBookDetailsNM>?
)

@Xml(name = "series_work")
data class SeriesWorkNM(
    @Element
    val series: SeriesNM?,
    @PropertyElement
    val user_position: String?,
    @PropertyElement
    val id: String?
)

@Xml(name = "series")
data class SeriesNM(
    @PropertyElement
    val series_works_count: String?,
    @PropertyElement
    val id: String?,
    @PropertyElement
    val title: String?,
    @PropertyElement
    val primary_work_count: String?
)

@Xml(name = "book")
data class ShortBookDetailsNM(
    @PropertyElement
    val id: String?,
    @PropertyElement
    val title: String?,
    @PropertyElement(name = "image_url")
    val coverImageUrl: String?,
    @PropertyElement(name = "small_image_url")
    val smallCoverImageUrl: String?
)

@Xml(name = "author")
data class AuthorNM(
    @PropertyElement(name = "small_image_url")
    val smallImageUrl: String?,
    @PropertyElement(name = "image_url")
    val imageUrl: String?,
    @PropertyElement
    val name: String?,
    @PropertyElement(name = "average_rating")
    val averageRating: String?,
    @PropertyElement
    val id: String?
)