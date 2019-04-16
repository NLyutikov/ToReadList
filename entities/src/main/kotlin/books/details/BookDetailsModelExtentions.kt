package ru.appkode.base.entities.core.books.details

import ru.appkode.base.entities.core.books.Format

fun List<AuthorUM>.toAuthorsLine(range: IntRange): String {
    return Format.toLine(this, range) { author -> author.name ?: "" }
}

fun List<ShelfUM>.toShelvesLine(range: IntRange): String {
    return Format.toLine(this, range) { shelf -> shelf.name ?: "" }
}
