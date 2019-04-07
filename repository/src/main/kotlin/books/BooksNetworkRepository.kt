package ru.appkode.base.repository.books

interface BooksNetworkRepository {
    fun getBookDetails(bookId: Int)

}