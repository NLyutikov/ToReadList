package ru.appkode.base.ui.books.lists.search.movies

import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.search.SearchController
import ru.appkode.base.ui.books.lists.search.SearchPresenter
import ru.appkode.base.ui.books.lists.search.books.BooksSearchPresenter
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class MoviesSearchController : SearchController() {

    override fun createPresenter(): SearchPresenter {
        return MoviesSearchPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }
}