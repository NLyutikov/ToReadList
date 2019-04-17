package ru.appkode.base.ui.books.lists.history

import io.reactivex.Observable
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

    override fun wishListIconCickedIntent(): Observable<Int> {
        return listAdapter.wishListIconClicked
    }

    override fun deleteIconClickedIntent(): Observable<Int> {
        return listAdapter.deleteIconClicked
    }
}