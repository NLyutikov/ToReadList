package ru.appkode.base.entities.core.movies.details

import movies.details.Genre
import ru.appkode.base.entities.core.books.Format

fun List<Genre>.toGenreLine(range: IntRange): String {
    return Format.toLine(this, range) { genre -> genre.name ?: "" }
}