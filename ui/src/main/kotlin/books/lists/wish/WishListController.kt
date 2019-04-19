package ru.appkode.base.ui.books.lists.wish

import io.reactivex.Observable
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.books.lists.adapters.CommonListAdapter
import ru.appkode.base.ui.books.lists.adapters.DragAndDrop
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class WishListController : CommonListController() {

    override fun getBooksListAdapter(): CommonListAdapter = WishListAdapter()

    override fun createPresenter(): CommonListPresenter {
        return WishListPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }

    override fun historyIconClickedIntent(): Observable<Int> {
        return listAdapter.historyIconClicked
    }

    override fun deleteIconClickedIntent(): Observable<Int> {
        return listAdapter.deleteIconClicked
    }
}

class WishListAdapter : CommonListAdapter(true, true), DragAndDrop {
    override fun adapter(): CommonListAdapter = this
}