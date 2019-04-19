package ru.appkode.base.entities.core.books.lists

import ru.appkode.base.entities.core.books.lists.history.HistorySM
import ru.appkode.base.entities.core.books.lists.wish.WishListSM

fun BookListItemUM.toWishListSM(order: Long = 0): WishListSM {
    return WishListSM(id, title, averageRating, imagePath, this.order ?: order)
}

fun BookListItemUM.toHistorySM(date: Long = 0): HistorySM {
    return HistorySM(id, title, averageRating, imagePath, date)
}