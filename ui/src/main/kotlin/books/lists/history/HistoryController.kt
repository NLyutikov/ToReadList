package ru.appkode.base.ui.books.lists.history

import books.lists.adapters.Swipe
import io.reactivex.Observable
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.books.lists.adapters.CommonListAdapter
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class HistoryController : CommonListController() {

    override fun getBooksListAdapter(): CommonListAdapter = HistoryAdapter()

    override fun createPresenter(): CommonListPresenter {
        return HistoryPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }

    override fun wishListIconCickedIntent(): Observable<Int> {
        return listAdapter.wishListIconClicked
    }

    override fun deleteIconClickedIntent(): Observable<Int> {
        return listAdapter.deleteIconClicked
    }
}

class HistoryAdapter : CommonListAdapter(true), Swipe {
    override fun delegateControlsAdapter(): CommonListAdapter = this
}