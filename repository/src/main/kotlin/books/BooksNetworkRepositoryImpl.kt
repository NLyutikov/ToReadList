package ru.appkode.base.repository.books

import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.ui.core.core.util.AppSchedulers

class BooksNetworkRepositoryImpl(
    val schedulers: AppSchedulers,
    val booksApi: BooksApi
) : BooksNetworkRepository {

    override fun getBookDetails(bookId: Int) {

    }

}