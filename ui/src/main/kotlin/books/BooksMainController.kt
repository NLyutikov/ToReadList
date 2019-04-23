package ru.appkode.base.ui.books

import android.view.MenuItem
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.bluelinelabs.conductor.Router
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_main_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.eventThrottleFirst
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.core.core.util.isOnlyControllersWithTagsInBackstack
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BooksMainController :
    BaseMviController<BooksMainScreen.ViewState, BooksMainScreen.View, BooksMainPresenter>(),
    BooksMainScreen.View,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var childRouter: Router

    private val searchItems: List<String> by lazy { listOf(
        resources!!.getString(R.string.search_dialog_movie_item),
        resources!!.getString(R.string.search_dialog_book_item)
    ) }

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.books_main_controller
        }
    }

    override fun initializeView(rootView: View) {
        childRouter = getChildRouter(books_main_lists_container)
        books_main_bottom_navigation.setOnNavigationItemSelectedListener(this)
    }

    override fun renderViewState(viewState: BooksMainScreen.ViewState) {
        fieldChanged(viewState, {it.showDialog}) {
            if(viewState.showDialog)
                MaterialDialog(activity!!).show {
                    title(res = R.string.search_dialog_title)
                    listItems(items = searchItems) { dialog, index, text ->
                        when(index) {
                            0 -> eventsRelay.accept(SHOW_SEARCH_MOVIE_LIST to Unit)
                            1 -> eventsRelay.accept(SHOW_SEARCH_BOOKS_LIST to Unit)
                        }
                    }
                    setOnCancelListener { eventsRelay.accept(DIALOG_CANCELED to Unit) }
                }
        }
    }

    override fun showListIntent(): Observable<String> {
        return eventsRelay.filterEvents(EVENT_ID_SHOW_LIST)
    }

    override fun showSearchList(): Observable<Unit> {
        return books_main_fab.clicks()
            .eventThrottleFirst()
    }

    override fun showBookSearchListIntent(): Observable<Unit> {
        return eventsRelay.filterEvents<Unit>(SHOW_SEARCH_BOOKS_LIST)
            .eventThrottleFirst()
    }

    override fun showMovieSearchListIntent(): Observable<Unit> {
        return eventsRelay.filterEvents<Unit>(SHOW_SEARCH_MOVIE_LIST)
            .eventThrottleFirst()
    }

    override fun dialogCanceledIntent(): Observable<Unit> {
        return eventsRelay.filterEvents<Unit>(DIALOG_CANCELED)
            .eventThrottleFirst()
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
const val SHOW_SEARCH_BOOKS_LIST = 76
const val SHOW_SEARCH_MOVIE_LIST = 34
const val DIALOG_CANCELED = 23