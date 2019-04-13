package ru.appkode.base.ui.books

import android.view.MenuItem
import android.view.View
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_main_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents

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

    override fun renderViewState(viewState: BooksMainScreen.ViewState) {
        fieldChanged(viewState, {state -> state.currentViewTag}) {
            showControllerByTag(viewState.currentViewTag)
        }
    }

    private fun showControllerByTag(tag: Int) {
        //TODO реализовать отображение wish list и history
//        if (childRouter.backstackSize > 1) {
//            childRouter.setBackstack(newBackstack(tag.toString(), childRouter.backstack), FadeChangeHandler())
//        } else {
//            if (childRouter.getControllerWithTag(tag.toString()) == null)
//                childRouter.pushController(ColorController().obtainFadeTransactionWithTag(tag.toString()))
//        }
    }

    private fun newBackstack(tag: String, backstack: List<RouterTransaction>): List<RouterTransaction> {
        val trans = backstack.find { it.tag() == tag }
        val newBackstack = ArrayList(backstack.filter { it.tag() != tag })
        newBackstack.add(trans)
        return newBackstack.toList()
    }

    override fun showListIntent(): Observable<Int> {
        return eventsRelay.filterEvents(EVENT_ID_SHOW_LIST)
    }

    override fun onNavigationItemSelected(item: MenuItem) = when(item.itemId) {
        R.id.wish_list_navigation ->  {
            eventsRelay.accept(EVENT_ID_SHOW_LIST to VIEW_TAG_1)
            true
        }
        R.id.history_navigation -> {
            eventsRelay.accept(EVENT_ID_SHOW_LIST to VIEW_TAG_2)
            true
        }
        else -> false
    }

    override fun handleBack(): Boolean {
        router.backstack.clear()
        activity?.finish()
        return false
    }

    override fun createPresenter(): BooksMainPresenter {
        return BooksMainPresenter(
            DefaultAppSchedulers,
            router
        )
    }

}

const val EVENT_ID_SHOW_LIST = 200


const val VIEW_TAG_1 = 1
const val VIEW_TAG_2 = 2

