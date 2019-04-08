package ru.appkode.base.entities.core.books.search

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GoodreadsResponse")
data class BookSearchNM(

    @Path("work")
    @PropertyElement
    val original_publication_year: Int?,

    @Path("work")
    @PropertyElement
    val original_publication_month: Int?,

    @Path("work")
    @PropertyElement
    val original_publication_day: Int?,

    @Path("work")
    @PropertyElement
    val average_rating: String?,

    @Path("work/best_book")
    @Element
    val best_book: BestBookNM?

)

@Xml(name = "best_book")
data class BestBookNM(

    @PropertyElement
    val id: Int?,
    @PropertyElement
    val title: String?,
    @Element
    val author: AuthorNM?,
    @PropertyElement
    val img_url: String?,
    @PropertyElement
    val small_img_url: String?

)

@Xml(name = "author")
data class AuthorNM(

    @PropertyElement
    val id: Int?,
    @PropertyElement
    val name: String?

)