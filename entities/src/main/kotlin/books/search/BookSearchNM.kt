package ru.appkode.base.entities.core.books.search

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GoodreadsResponse")
data class BookSearchNM(
    @Path("search/results")
    @Element
    val work: List<SearchResultNM>?
)

@Xml(name = "work")
data class SearchResultNM(
    @PropertyElement
    val average_rating: Double?,

    @Path("best_book")
    @PropertyElement
    val id: Long,

    @Path("best_book")
    @PropertyElement
    val title: String?,

    @Path("best_book")
    @PropertyElement
    val image_url: String?
)