package ru.appkode.base.ui.books.search

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.details.BookDetailsController
import ru.appkode.base.ui.books.search.BooksSearchScreen.View
import ru.appkode.base.ui.core.core.*
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import java.util.concurrent.TimeUnit

sealed class ScreenAction

data class LoadPage(val query: String, val page: Int) : ScreenAction()
data class LoadPageState(val state: LceState<List<BookListItemUM>>) : ScreenAction()
data class SearchBook(val inputText: String) : ScreenAction()
data class ShowImage(val url: String) : ScreenAction()
object DismissImage : ScreenAction()
object RepeatSearch : ScreenAction()
data class ItemClicked(val position: Int) : ScreenAction()

class BooksSearchPresenter(
    schedulers: AppSchedulers,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router
) : BasePresenter<View, BooksSearchScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(View::searchBookIntent)
                .map { SearchBook(it) }
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged(),
            intent(View::loadPageIntent)
                .map { LoadPage(it.first, it.second) },
            intent(View::showImageIntent)
                .map { ShowImage(it) },
            intent(View::dismissImageIntent)
                .map { DismissImage },
            intent(View::repeatSearchIntent)
                .map { RepeatSearch },
            intent(View::itemClickedIntent)
                .map { ItemClicked(it) }
        )
    }

    override fun reduceViewState(
        previousState: BooksSearchScreen.ViewState,
        action: ScreenAction
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when (action) {
            is RepeatSearch -> processRepeatSearch(previousState)
            is DismissImage -> previousState.copy(url = null) to null
            is ShowImage -> processShowImage(previousState, action)
            is SearchBook -> processSearchBook(previousState, action)
            is LoadPage -> processLoadPage(previousState, action)
            is ItemClicked -> processItemClicked(previousState, action)
            is LoadPageState -> processLoadPageState(previousState, action)
        }
    }

    private fun loadBooks(text: String, page: Int): Observable<List<BookListItemUM>> {
        return networkRepository.getBookSearch(text, page)
    }

    private fun processShowImage(
        previousState: BooksSearchScreen.ViewState,
        action: ShowImage
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {

        return previousState.copy(url = action.url) to null
    }

    private fun processSearchBook(
        previousState: BooksSearchScreen.ViewState,
        action: SearchBook
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        val isCorrectQuery = !action.inputText.isBlank() && action.inputText.length > MIN_QUERY_LENGTH
        return previousState.copy(
            query = action.inputText,
            page = 0,
            list = if(isCorrectQuery) emptyList() else previousState.list
        ) to commandOn(isCorrectQuery, {}) {
             Observable.just(LoadPage(action.inputText, 1) as ScreenAction)
        }
    }

    private fun processRepeatSearch(
        previousState: BooksSearchScreen.ViewState
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            page = 0,
            list = emptyList()
        ) to command(
            Observable.just(LoadPage(previousState.query!!, 1) as ScreenAction)
        )
    }

    private fun processLoadPage(
        previousState: BooksSearchScreen.ViewState,
        action: LoadPage
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            loadBooks(action.query, action.page)
                .doLceAction { LoadPageState(it) }
        )
    }


    private fun processLoadPageState(
        previousState: BooksSearchScreen.ViewState,
        action: LoadPageState
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var list = previousState.list
        var page = previousState.page
        if (action.state.isContent) {
            list = list.plus(action.state.asContent())
            page += 1
        }
        return previousState.copy(
            booksSearchState = action.state,
            list = list,
            page = page
        ) to null
    }

    private fun processItemClicked(
        previousState: BooksSearchScreen.ViewState,
        action: ItemClicked
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command {
            router.pushController(BookDetailsController.createController(
                previousState.list[action.position].id
            ).obtainHorizontalTransaction())
        }
    }

    override fun createInitialState(): BooksSearchScreen.ViewState {
        return BooksSearchScreen.ViewState(
            booksSearchState = LceState.Content(emptyList()),
            url = null,
            query = null,
            page = 0,
            list = emptyList()
        )
    }
}

private const val MIN_QUERY_LENGTH = 1
