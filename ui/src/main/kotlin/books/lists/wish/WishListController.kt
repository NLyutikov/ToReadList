package ru.appkode.base.ui.books.lists.wish

import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListAdapter
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.books.lists.CommonListScreen
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class WishListController : CommonListController() {

    override val listAdapter: CommonListAdapter = CommonListAdapter(true, true)

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