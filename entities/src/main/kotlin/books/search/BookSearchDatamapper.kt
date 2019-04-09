package ru.appkode.base.entities.core.books.search

import ru.appkode.base.entities.core.util.requireField

fun BookSearchNM.toUiModel(): BookSearchUM {
    return BookSearchUM(
        original_publication_year,
        original_publication_month,
        original_publication_day,
        average_rating,
        best_book?.toBestBookUM()
    )
}

fun List<BestBookNM>.toBestBookUM(): List<BestBookUM> {
    return map { best_book -> best_book.toBestBookUM() }
}

fun BestBookNM.toBestBookUM(): BestBookUM {
    return BestBookUM(
        id,
        title,
        author?.toAuthorUM(),
        img_url,
        small_img_url
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