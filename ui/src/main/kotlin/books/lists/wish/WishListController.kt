package ru.appkode.base.ui.books.lists.wish

import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListAdapter
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.books.lists.CommonListScreen
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class WishListController : CommonListController() {

    override val listAdapter: CommonListAdapter = CommonListAdapter(true)

    override fun createPresenter(): CommonListPresenter {
        return WishListPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }
}