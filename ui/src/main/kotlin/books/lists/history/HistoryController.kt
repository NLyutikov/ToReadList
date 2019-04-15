package ru.appkode.base.ui.books.lists.history

import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListAdapter
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class HistoryController : CommonListController() {

    override val listAdapter: CommonListAdapter = CommonListAdapter(true)

    override fun createPresenter(): CommonListPresenter {
        return HistoryPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }

    override fun loadNextPageOfBooksIntent(): Observable<Int> {
        return books_list_recycler.scrollEvents().filter { false }.map { 1 }
    }

}