package ru.appkode.base.entities.core.books.details

import android.text.Html
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.util.requireField
import java.util.*

fun BookDetailsNM.toUiModel(): BookDetailsUM {
    return BookDetailsUM (
        id = goodReadsBookId.requireField("BookDetails.goodReadsId").toLong(),
        title = title,
        isbn = isbn,
        isbn13 = isbn13,
        language = if (!languageCode.isNullOrBlank()) Locale(languageCode).language else null,
        coverImageUrl = coverImageUrl,
        smallCoverImageUrl = smallCoverImageUrl,
        pagesNumber = pagesNumber?.toInt(),
        description = Html.fromHtml(description).toString(),
        ratingsCount = ratingsCount?.toInt(),
        averageRating = averageRating?.toDouble(),
        shelves = shelves?.toShelfUM()?.sortedByDescending { shelf -> shelf.count },
        authors = authors?.toAuthorUM(),
        similarBooks = similarBooks?.toBookDetailsUM()
    )
}

fun List<ShortBookDetailsNM>.toBookDetailsUM(): List<BookDetailsUM> {
    return map { book -> book.toBookDetailsUM()}
}

fun ShortBookDetailsNM.toBookDetailsUM(): BookDetailsUM {
    return BookDetailsUM(
        id.requireField("similarBookNM.id").toLong(),
        title,
        coverImageUrl = coverImageUrl,
        smallCoverImageUrl = smallCoverImageUrl
    )
}

fun List<AuthorNM>.toAuthorUM(): List<AuthorUM> {
    return map { author -> author.toAuthorUM() }
}

fun AuthorNM.toAuthorUM(): AuthorUM {
    return AuthorUM(
        id.requireField("authorNM.id").toLong(),
        name
    )
}

fun List<ShelfNM>.toShelfUM(): List<ShelfUM> {
    return map { shelf -> shelf.toShelfUM() }
}

fun ShelfNM.toShelfUM(): ShelfUM {
    return ShelfUM(
        name,
        count?.toInt()
    )
}

fun BookDetailsUM.toBookListItemUM(): BookListItemUM {
    return BookListItemUM(
        id = id,
        title = title,
        averageRating = averageRating,
        imagePath = coverImageUrl,
        isInHistory = isInWishList,
        isInWishList = isInWishList
    )
}