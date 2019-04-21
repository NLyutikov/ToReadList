package ru.appkode.base.ui.books.lists.search.books

import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.search.SearchController
import ru.appkode.base.ui.books.lists.search.SearchPresenter
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class BooksSearchController : SearchController() {

    override fun createPresenter(): SearchPresenter {
        return BooksSearchPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }
}