package ru.appkode.base.ui.books

import android.view.MenuItem
import android.view.View
import com.bluelinelabs.conductor.Router
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_main_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.core.core.util.isOnlyControllersWithTagsInBackstack
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BooksMainController :
    BaseMviController<BooksMainScreen.ViewState, BooksMainScreen.View, BooksMainPresenter>(),
    BooksMainScreen.View,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var childRouter: Router

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.books_main_controller
        }
    }

    override fun initializeView(rootView: View) {
        childRouter = getChildRouter(books_main_lists_container)
        books_main_bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun renderViewState(viewState: BooksMainScreen.ViewState) {}

    override fun showListIntent(): Observable<String> {
        return eventsRelay.filterEvents(EVENT_ID_SHOW_LIST)
    }

    override fun showSearchList(): Observable<Unit> {
        return books_main_fab.clicks().throttleFirst(700, TimeUnit.MILLISECONDS)
    }

    override fun onNavigationItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.wish_list_navigation ->  {
            eventsRelay.accept(EVENT_ID_SHOW_LIST to WISH_LIST_CONTROLLER_TAG)
            true
        }
        R.id.history_navigation -> {
            eventsRelay.accept(EVENT_ID_SHOW_LIST to HISTORY_CONTROLLER_TAG)
            true
        }
        else -> false
    }

    /**
     * Если в backstack лежат только контроллеры отвечающие за навигацию то убиваем активность, к которой привязанны
     */
    override fun handleBack(): Boolean {
        if (!childRouter.isOnlyControllersWithTagsInBackstack(WISH_LIST_CONTROLLER_TAG, HISTORY_CONTROLLER_TAG))
            return super.handleBack()
        activity?.finish()
        return false
    }

    override fun createPresenter(): BooksMainPresenter {
        return BooksMainPresenter(
            DefaultAppSchedulers,
            childRouter,
            router
        )
    }

}

const val EVENT_ID_SHOW_LIST = 200

const val WISH_LIST_CONTROLLER_TAG = "1"
const val HISTORY_CONTROLLER_TAG = "2"