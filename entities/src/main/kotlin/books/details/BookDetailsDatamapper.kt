package ru.appkode.base.entities.core.books.details

import ru.appkode.base.entities.core.util.requireField
import java.util.*

fun BookDetailsNM.toUiModel(): BookDetailsUM {
    return BookDetailsUM (
        goodReadsBookId.requireField("BookDetails.goodReadsId").toInt(),
        title,
        isbn,
        isbn13,
        if (!languageCode.isNullOrBlank()) Locale(languageCode).language else null,
        coverImageUrl,
        smallCoverImageUrl,
        pagesNumber?.toInt(),
        description,
        ratingsCount?.toInt(),
        authors?.toAuthorUM(),
        similarBooks?.toBookDetailsUM()
    )
}

fun List<ShortBookDetailsNM>.toBookDetailsUM(): List<BookDetailsUM> {
    return map { book -> book.toBookDetailsUM()}
}

fun ShortBookDetailsNM.toBookDetailsUM(): BookDetailsUM {
    return BookDetailsUM(
        id.requireField("similarBookNM.id").toInt(),
        title,
        smallCoverImageUrl = smallCoverImageUrl
    )
}

fun List<AuthorNM>.toAuthorUM(): List<AuthorUM> {
    return map { author -> author.toAuthorUM() }
}

fun AuthorNM.toAuthorUM(): AuthorUM {
    return AuthorUM(
        id.requireField("authorNM.id").toInt(),
        name
    )
}