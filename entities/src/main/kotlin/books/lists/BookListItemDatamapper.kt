package ru.appkode.base.entities.core.books.lists

import ru.appkode.base.entities.core.books.lists.history.HistorySM
import ru.appkode.base.entities.core.books.lists.wish.WishListSM

fun BookListItemUM.toWishListSM(): WishListSM {
    return WishListSM(id, title, averageRating, imagePath)
}

fun BookListItemUM.toHistorySM(): HistorySM {
    return HistorySM(id, title, averageRating, imagePath)
}