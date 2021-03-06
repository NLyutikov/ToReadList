package ru.appkode.base.entities.core.books.details

import paperparcel.PaperParcel
import paperparcel.PaperParcelable

@PaperParcel
data class BookDetailsUM(
    val id: Long,
    val title: String? = null,
    val isbn: String? = null,
    val isbn13: String? = null,
    val language: String? = null,
    val coverImageUrl: String? = null,
    val smallCoverImageUrl: String? = null,
    val pagesNumber: Int? = null,
    val description: String? = null,
    val ratingsCount: Int? = null,
    val averageRating: Double? = null,
    val shelves: List<ShelfUM>? = null,
    val authors: List<AuthorUM>? = null,
    val similarBooks: List<BookDetailsUM>? = null,
    val isInWishList: Boolean = false,
    val isInHistory: Boolean = false
): PaperParcelable {
    companion object  {
        @JvmField val CREATOR = PaperParcelBookDetailsUM.CREATOR
    }
}

@PaperParcel
data class AuthorUM(
    val id: Long,
    val name: String? = null
): PaperParcelable {
    companion object  {
        @JvmField val CREATOR = PaperParcelAuthorUM.CREATOR
    }
}

@PaperParcel
data class ShelfUM (
    val name: String? = null,
    val count: Int? = null
): PaperParcelable {
    companion object  {
        @JvmField val CREATOR = PaperParcelShelfUM.CREATOR
    }
}