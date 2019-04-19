package ru.appkode.base.ui.books.search

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_search_controller.*
import kotlinx.android.synthetic.main.network_error.*
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.LceState
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
        books_search_toolbar.setNavigationOnClickListener { router.handleBack() }

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
            renderSearchState(viewState.booksSearchState)
            books_search_toolbar_search.onActionViewExpanded()
        }
        fieldChanged(viewState, { it.url.orEmpty() }) {
            if (viewState.url.isNullOrBlank()) return@fieldChanged
            StfalconImageViewer.Builder<String>(activity, listOf(viewState.url)) { view, url ->
                Picasso.get().load(url).into(view)
            }
                .withDismissListener { eventsRelay.accept(EVENT_ID_IMAGE_DISMISS to Unit) }
                .show()
        }
    }

    private fun renderSearchState(searchState: LceState<List<BookListItemUM>>) {
        books_search_loading.isVisible = searchState.isLoading
        books_search_recycler.isVisible = searchState.isContent
        network_error_screen_container.isVisible = searchState.isError
        if (searchState.isContent) {
            adapter.data = searchState.asContent()
        }
    }

    override fun itemClickedIntent(): Observable<Long> {
        return adapter.itemClicked
    }

    override fun searchBookIntent(): Observable<String> {
        return eventsRelay.filterEvents(EVENT_ID_SEARCH_CHANGED)
    }

    override fun showImageIntent(): Observable<String> {
        return adapter.imageClicked
    }

    override fun dismissImageIntent(): Observable<Unit> {
        return eventsRelay.filterEvents(EVENT_ID_IMAGE_DISMISS)
    }

    override fun repeatSearchIntent(): Observable<Unit> {
        return network_error_screen_reload_btn.clicks()
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
private const val EVENT_ID_IMAGE_DISMISS = 1
