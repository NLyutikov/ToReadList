package ru.appkode.base.ui.books.search

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.search.BooksSearchScreen.View
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.commandOn
import ru.appkode.base.ui.core.core.util.AppSchedulers
import java.util.concurrent.TimeUnit

sealed class ScreenAction

data class UpdateList(val state: LceState<List<BookUM>>) : ScreenAction()
data class SearchBook(val inputText: String) : ScreenAction()
data class ShowImage(val url: String) : ScreenAction()
object DismissImage : ScreenAction()

class BooksSearchPresenter(
    schedulers: AppSchedulers,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router
) : BasePresenter<View, BooksSearchScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(View::searchBookIntent)
                .map { SearchBook(it) }
                .debounce(1000, TimeUnit.MILLISECONDS)
                .distinctUntilChanged(),
            intent(View::showImageIntent)
                .map { ShowImage(it) },
            intent(View::dismissImageIntent)
                .map { DismissImage }
        )
    }

    override fun reduceViewState(
        previousState: BooksSearchScreen.ViewState,
        action: ScreenAction
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when (action) {
            is DismissImage -> previousState.copy(url = null) to null
            is ShowImage -> processShowImage(previousState, action)
            is SearchBook -> processSearchBook(previousState, action)
            is UpdateList -> processUpdateList(previousState, action)
        }
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
        return previousState to commandOn(isCorrectQuery, {}) {
            networkRepository.getBookSearch(action.inputText)
                .toObservable()
                .doLceAction { UpdateList(it) }
        }
    }

    private fun processUpdateList(
        previousState: BooksSearchScreen.ViewState,
        action: UpdateList
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            booksSearchState = action.state
        ) to null
    }

    override fun createInitialState(): BooksSearchScreen.ViewState {
        return BooksSearchScreen.ViewState(
            booksSearchState = LceState.Content(emptyList()),
            url = null
        )
    }
}

private const val MIN_QUERY_LENGTH = 1
