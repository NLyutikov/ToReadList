package ru.appkode.base.ui.books.search

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_search_controller.*
import kotlinx.android.synthetic.main.network_error.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents

class BooksSearchController :
    BaseMviController<
            BooksSearchScreen.ViewState,
            BooksSearchScreen.View,
            BooksSearchPresenter>(),
    BooksSearchScreen.View {
    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource: Int
                get() = R.layout.books_search_controller
        }
    }

    private val adapter: BooksSearchAdapter = BooksSearchAdapter()

    override fun initializeView(rootView: View) {
        books_search_toolbar_search.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) eventsRelay.accept(EVENT_ID_SEARCH_CHANGED to newText)
                return true
            }

        })
        books_search_recycler.layoutManager = LinearLayoutManager(applicationContext)
        books_search_recycler.adapter = adapter
    }

    override fun renderViewState(viewState: BooksSearchScreen.ViewState) {
        fieldChanged(viewState, { it.booksSearchState }) {
            books_search_loading.isVisible = viewState.booksSearchState.isLoading
            books_search_recycler.isVisible = viewState.booksSearchState.isContent
            network_error_screen_container.isVisible = viewState.booksSearchState.isError
        }
        if (viewState.booksSearchState.isContent) {
            adapter.data = viewState.booksSearchState.asContent()
        }
    }

    override fun searchBookIntent(): Observable<String> {
        return eventsRelay.filterEvents(EVENT_ID_SEARCH_CHANGED)
    }

    override fun createPresenter(): BooksSearchPresenter {
        return BooksSearchPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }
}

private const val EVENT_ID_SEARCH_CHANGED = 0
