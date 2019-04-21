package ru.appkode.base.ui.books.lists.search.books

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.lists.search.SearchPresenter
import ru.appkode.base.ui.core.core.util.AppSchedulers

class BooksSearchPresenter(
    schedulers: AppSchedulers,
    private val localRepository: BooksLocalRepository,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router
) : SearchPresenter(schedulers, networkRepository, localRepository, router) {

    override fun loadContent(text: String, page: Int): Observable<List<BookListItemUM>> {
            return networkRepository.getBookSearch(text, localRepository, page)
    }
}