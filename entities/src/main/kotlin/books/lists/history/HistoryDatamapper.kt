package ru.appkode.base.entities.core.books.lists.history

import ru.appkode.base.entities.core.books.lists.BookListItemUM

fun HistorySM.toBookListItemUM(): BookListItemUM {
    return BookListItemUM(id, title, averageRating, imagePath, isInHistory = true)
}

fun List<HistorySM>.toBookListItemUM(): List<BookListItemUM> {
    return map { book -> book.toBookListItemUM() }
}