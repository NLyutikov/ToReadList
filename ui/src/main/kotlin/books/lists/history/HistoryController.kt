package ru.appkode.base.ui.books.lists.history

import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.books.lists.CommonListAdapter
import ru.appkode.base.ui.books.lists.CommonListController
import ru.appkode.base.ui.books.lists.CommonListPresenter
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class HistoryController : CommonListController() {

    //FIXME когда будет реализована поддержка бд заменит парметр на true
    override val listAdapter: CommonListAdapter = CommonListAdapter(false)

    override fun createPresenter(): CommonListPresenter {
        return HistoryPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }

}