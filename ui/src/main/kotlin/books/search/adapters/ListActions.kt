package ru.appkode.base.ui.books.search.adapters

import ru.appkode.base.entities.core.books.search.BookSearchUM
import ru.appkode.base.entities.core.books.search.BookUM

interface ListActions {

    //посчитать элементы
    fun count(): Int

    //подвинуть элемент
    fun changeItemPosition(fromPosition: Int, toPosition: Int)

    //получить элемент
    fun getItem(index: Int): BookUM
}