package ru.appkode.base.ui.books.lists.wish

import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListAdapter
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.books.lists.CommonListScreen
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class WishListController : CommonListController() {
    //TODO когда будет реализована поддержка бд заменит парметр на true
    override val listAdapter: CommonListAdapter = CommonListAdapter(false)

    override fun createPresenter(): CommonListPresenter {
        return WishListPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }
}