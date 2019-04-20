package ru.appkode.base.ui.books.search

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.details.BookDetailsController
import ru.appkode.base.ui.books.search.BooksSearchScreen.View
import ru.appkode.base.ui.core.core.*
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import java.util.concurrent.TimeUnit

sealed class ScreenAction

data class UpdateList(val state: LceState<List<BookListItemUM>>) : ScreenAction()
data class SearchBook(val inputText: String) : ScreenAction()
data class ShowImage(val url: String) : ScreenAction()
object DismissImage : ScreenAction()
object RepeatSearch : ScreenAction()
data class ItemClicked(val position: Long) : ScreenAction()

class BooksSearchPresenter(
    schedulers: AppSchedulers,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router
) : BasePresenter<View, BooksSearchScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(View::searchBookIntent)
                .map { SearchBook(it) }
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged(),
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
            is RepeatSearch -> processRepeatSearch(previousState, action)
            is DismissImage -> previousState.copy(url = null) to null
            is ShowImage -> processShowImage(previousState, action)
            is SearchBook -> processSearchBook(previousState, action)
            is UpdateList -> processUpdateList(previousState, action)
            is ItemClicked -> processItemClicked(previousState, action)
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
        return previousState.copy(query = action.inputText) to commandOn(isCorrectQuery, {}) {
            networkRepository.getBookSearch(action.inputText)
                .doLceAction { UpdateList(it) }
        }
    }

    private fun processRepeatSearch(
        previousState: BooksSearchScreen.ViewState,
        action: RepeatSearch
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            networkRepository.getBookSearch(previousState.query!!)
                .doLceAction { UpdateList(it) }
        )
    }


    private fun processUpdateList(
        previousState: BooksSearchScreen.ViewState,
        action: UpdateList
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            booksSearchState = action.state
        ) to null
    }

    private fun processItemClicked(
        previousState: BooksSearchScreen.ViewState,
        action: ItemClicked
    ): Pair<BooksSearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command { router.pushController(BookDetailsController.createController(action.position).obtainHorizontalTransaction())}
    }

    override fun createInitialState(): BooksSearchScreen.ViewState {
        return BooksSearchScreen.ViewState(
            booksSearchState = LceState.Content(emptyList()),
            url = null,
            query = null
        )
    }
}

private const val MIN_QUERY_LENGTH = 1
