package ru.appkode.base.entities.core.books.lists.wish

import ru.appkode.base.entities.core.books.lists.BookListItemUM

fun WishListSM.toBookListItemUM(): BookListItemUM {
    return BookListItemUM(id, title, averageRating, imagePath, order, isInWishList = true)
}

fun List<WishListSM>.toBookListItemUM(): List<BookListItemUM> {
    return map { book -> book.toBookListItemUM() }
}