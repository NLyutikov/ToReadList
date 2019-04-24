package ru.appkode.base.ui.books.lists.search

import books.details.books.BookDetailsScreen
import books.details.books.HistoryState
import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.toBookListItemUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.lists.ChangeItemPosition
import ru.appkode.base.ui.books.lists.CommonListScreen
import ru.appkode.base.ui.books.lists.search.SearchScreen.View
import ru.appkode.base.ui.core.core.*
import ru.appkode.base.ui.core.core.util.AppSchedulers
import java.util.concurrent.TimeUnit

sealed class ScreenAction

data class LoadPage(val query: String, val page: Int) : ScreenAction()
data class LoadPageState(val state: LceState<List<BookListItemUM>>) : ScreenAction()
data class SearchBook(val inputText: String) : ScreenAction()
data class ShowImage(val url: String) : ScreenAction()
object DismissImage : ScreenAction()
object RepeatSearch : ScreenAction()
data class ItemClicked(val position: Int) : ScreenAction()
object Refresh : ScreenAction()
object InBaseStateChanged : ScreenAction()
data class InBaseStateChangedState(val state: LceState<List<BookListItemUM>>) : ScreenAction()
data class WishListIconPressed(val position: Int) : ScreenAction()
data class HistoryIconPressed(val position: Int) : ScreenAction()
data class ChangeList(val changeAction: (List<BookListItemUM>) -> (List<BookListItemUM>)) : ScreenAction()

abstract class SearchPresenter(
    schedulers: AppSchedulers,
    private val networkRepository: BooksNetworkRepository,
    private val localRepository: BooksLocalRepository,
    private val router: Router
) : BasePresenter<View, SearchScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(View::searchBookIntent)
                .map { SearchBook(it) }
                .debounce(500, TimeUnit.MILLISECONDS)
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
                .map { ItemClicked(it) },
            intent(View::refreshIntent)
                .map { Refresh },
            intent(View::historyIconPressedIntent)
                .map { HistoryIconPressed(it) },
            intent(View::wishIconPressedIntent)
                .map{ WishListIconPressed(it) },
            intent{ viewStateObservable.map { it.list } }
                .map { InBaseStateChanged }
        )
    }

    override fun reduceViewState(
        previousState: SearchScreen.ViewState,
        action: ScreenAction
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when (action) {
            is RepeatSearch -> processRepeatSearch(previousState)
            is DismissImage -> previousState.copy(url = null) to null
            is ShowImage -> processShowImage(previousState, action)
            is SearchBook -> processSearchBook(previousState, action)
            is LoadPage -> processLoadPage(previousState, action)
            is ItemClicked -> processItemClicked(previousState, action)
            is LoadPageState -> processLoadPageState(previousState, action)
            is Refresh -> processRefresh(previousState)
            is InBaseStateChanged -> processInBaseStateChanged(previousState)
            is InBaseStateChangedState -> processInBaseStateChangedState(previousState, action)
            is HistoryIconPressed -> processHistoryIconPressed(previousState, action)
            is WishListIconPressed -> processWishListIconPressed(previousState, action)
            is ChangeList -> processChangeList(previousState, action)
        }
    }

    protected abstract fun loadContent(text: String, page: Int): Observable<List<BookListItemUM>>

    private fun isCorrectQuery(query: String?): Boolean {
        return !query.isNullOrBlank() && query.length > MIN_QUERY_LENGTH
    }

    private fun processShowImage(
        previousState: SearchScreen.ViewState,
        action: ShowImage
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {

        return previousState.copy(url = action.url) to null
    }

    private fun processSearchBook(
        previousState: SearchScreen.ViewState,
        action: SearchBook
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            query = action.inputText,
            page = 0,
            list = if(isCorrectQuery(action.inputText)) emptyList() else previousState.list
        ) to commandOn(isCorrectQuery(action.inputText), {}) {
             Observable.just(LoadPage(action.inputText, 1) as ScreenAction)
        }
    }

    private fun processRepeatSearch(
        previousState: SearchScreen.ViewState
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            page = 0,
            list = emptyList()
        ) to command(
            Observable.just(LoadPage(previousState.query!!, 1) as ScreenAction)
        )
    }

    private fun processLoadPage(
        previousState: SearchScreen.ViewState,
        action: LoadPage
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            loadContent(action.query, action.page)
                .doLceAction { LoadPageState(it) }
        )
    }

    private fun processLoadPageState(
        previousState: SearchScreen.ViewState,
        action: LoadPageState
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var list = previousState.list
        var page = previousState.page
        var isRefreshing = previousState.isRefreshing
        if (action.state.isContent) {
            list = list.plus(action.state.asContent())
            page += 1
            isRefreshing = false
        }
        if (action.state.isError)
            isRefreshing = false
        return previousState.copy(
            booksSearchState = action.state,
            list = list,
            page = page,
            isRefreshing = isRefreshing
        ) to null
    }

    protected abstract fun processItemClicked(
        previousState: SearchScreen.ViewState,
        action: ItemClicked
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?>

    private fun processRefresh(
        previousState: SearchScreen.ViewState
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            isRefreshing = true
        ) to command(
            Observable.just(SearchBook(previousState.query ?: "") as ScreenAction)
        )
    }

    private fun processInBaseStateChanged(
        previousState: SearchScreen.ViewState
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            localRepository.getInBaseState(previousState.list)
                .doLceAction { InBaseStateChangedState(it) }
        )
    }

    private fun processInBaseStateChangedState(
        previousState: SearchScreen.ViewState,
        action: InBaseStateChangedState
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var list = previousState.list
        if (action.state.isContent)
            list = action.state.asContent()
        return previousState.copy(list = list) to null
    }

    private fun processHistoryIconPressed(
        previousState: SearchScreen.ViewState,
        action: HistoryIconPressed
    ) : Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        val item = previousState.list[action.position]
        var com: Command<Observable<ScreenAction>>? = null
        val act =  ChangeList { list ->
            val list = ArrayList(list)
            list[action.position] = list[action.position].copy(
                isInWishList = false,
                isInHistory = !list[action.position].isInHistory
            )
            list.toList()
        }
        if (item.isInHistory)
            com = command(
                localRepository.deleteFromHistory(item)
                    .doAction { act }
            )
        else
            com = command(
            localRepository.addToHistory(item)
                    .doAction { act }
            )

        return previousState to com
    }

    private fun processWishListIconPressed(
        previousState: SearchScreen.ViewState,
        action: WishListIconPressed
    ) : Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        val item = previousState.list[action.position]
        var com: Command<Observable<ScreenAction>>? = null
        val act =  ChangeList { list ->
            val list = ArrayList(list)
            list[action.position] = list[action.position].copy(
                isInHistory = false,
                isInWishList = !list[action.position].isInWishList
            )
            list.toList()
        }
        if (item.isInWishList)
            com = command(
                localRepository.deleteFromWishList(item)
                    .doAction { act }
            )
        else
            com = command(
                localRepository.addToWishList(item)
                    .doAction { act }
            )

        return previousState to com
    }

    private fun processChangeList(
        previousState: SearchScreen.ViewState,
        action: ChangeList
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(list = action.changeAction(previousState.list)) to null
    }

    override fun createInitialState(): SearchScreen.ViewState {
        return SearchScreen.ViewState(
            booksSearchState = LceState.Content(emptyList()),
            url = null,
            query = null,
            page = 0,
            list = emptyList(),
            isRefreshing = false
        )
    }
}

private const val MIN_QUERY_LENGTH = 1
