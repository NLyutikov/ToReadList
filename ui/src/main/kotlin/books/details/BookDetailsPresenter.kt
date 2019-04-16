package ru.appkode.base.ui.books.details

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import io.reactivex.functions.Function3
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toBookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction
import timber.log.Timber

sealed class ScreenAction

data class LoadBookDetails(val state: LceState<BookDetailsUM>) : ScreenAction()
data class ShowSimilarBook(val bookId: Long) : ScreenAction()
object ShowMoreInformation : ScreenAction()
object HistoryBtnPressed : ScreenAction()
object WishListBtnPressed : ScreenAction()
data class HistoryState(val state: LceState<Unit>) : ScreenAction()
data class WishListState(val state: LceState<Unit>) : ScreenAction()
data class InDbStatus(val state: LceState<BookDetailsUM>) : ScreenAction()

class BookDetailsPresenter(
    schedulers: AppSchedulers,
    private val networkRepository: BooksNetworkRepository,
    private val localRepository: BooksLocalRepository,
    private val router: Router,
    private val bookId: Long
) : BasePresenter<BookDetailsScreen.View, BookDetailsScreen.ViewState, ScreenAction>(schedulers){

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(BookDetailsScreen.View::showMoreInfoIntent)
                .map{ShowMoreInformation},
            intent(BookDetailsScreen.View::showSimilarBookIntent)
                .map{ShowSimilarBook(it)},
            intent(BookDetailsScreen.View::wishListBtnPressed)
                .map { WishListBtnPressed },
            intent(BookDetailsScreen.View::historyBtnPressed)
                .map { HistoryBtnPressed },
            intent { networkRepository.getBookDetails(bookId).onErrorReturn { null }}
                .doLceAction { LoadBookDetails(it) }
                .doOnError { e -> Timber.e(e.message) }
                .onErrorReturn { e ->
                    LoadBookDetails(LceState.Error(
                        e.message ?: "unknown error",
                        BookDetailsUM(-1)
                    ))
                }
        )
    }

    override fun reduceViewState(
        previousState: BookDetailsScreen.ViewState,
        action: ScreenAction
    ): Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when(action) {
            is ShowSimilarBook -> processShowSimilarBook(previousState, action)
            is LoadBookDetails -> processLoadBookDetails(previousState, action)
            is ShowMoreInformation -> processShowMoreInformation(previousState, action)
            is HistoryBtnPressed -> processHistoryBtnPressed(previousState, action)
            is WishListBtnPressed -> processWishListBtnPressed(previousState, action)
            is HistoryState -> processHistoryState(previousState, action)
            is WishListState -> processWishListState(previousState, action)
            is InDbStatus -> processInDbStatus(previousState, action)
        }
    }

    private fun getInBaseState(book: BookDetailsUM): Observable<BookDetailsUM> {
        val isInHistory = localRepository.isInHistory(book.toBookListItemUM())
        val isInWishLis = localRepository.isInWishList(book.toBookListItemUM())
        val mBook = Observable.just(book)
        return  Observable.zip(
            mBook,
            isInHistory,
            isInWishLis,
            Function3 <BookDetailsUM, Boolean, Boolean, BookDetailsUM> { book, inHist, inWish ->
                book.copy(
                    isInHistory = inHist,
                    isInWishList = inWish
                )
            }
        )
    }

    private fun processLoadBookDetails(
        previousState: BookDetailsScreen.ViewState,
        action: LoadBookDetails
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var bookDetails: BookDetailsUM? = previousState.bookDetails
        var com: Command<Observable<ScreenAction>>? = null
        if (action.state.isContent) {
            bookDetails = action.state.asContent()
            com = command(
                getInBaseState(bookDetails).doLceAction { lceState ->  InDbStatus(lceState)}
            )
        }
        return previousState.copy(
            bookDetails = bookDetails,
            bookDetailsState = action.state
        ) to com
    }

    private fun processInDbStatus(
        previousState: BookDetailsScreen.ViewState,
        action: InDbStatus
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var details = previousState.bookDetails
        if (action.state.isContent)
            details = action.state.asContent()
        return previousState.copy(
            bookDetails = details
        ) to null
    }

    private fun processShowSimilarBook(
        previousState: BookDetailsScreen.ViewState,
        action: ShowSimilarBook
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command {
            router.pushController(
                BookDetailsController.createController(action.bookId).obtainHorizontalTransaction()
            )
        }
    }

    private fun processShowMoreInformation(
        previousState: BookDetailsScreen.ViewState,
        action: ShowMoreInformation
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command {
            //TODO показать доп. информацию о книге
        }
    }

    private fun processHistoryBtnPressed(
        previousState: BookDetailsScreen.ViewState,
        action: HistoryBtnPressed
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var com: Command<Observable<ScreenAction>>? = null
        if (previousState.bookDetails != null) {
            if (previousState.bookDetails.isInHistory)
                com = command(
                    localRepository.deleteFromHistory(previousState.bookDetails.toBookListItemUM())
                        .doLceAction { HistoryState(it) }
                )
            else
                com = command(
                    localRepository.addToHistory(previousState.bookDetails.toBookListItemUM())
                        .doLceAction { HistoryState(it) }
                )
        }
        return previousState to com
    }


    private fun processHistoryState(
        previousState: BookDetailsScreen.ViewState,
        action: HistoryState
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var details = previousState.bookDetails
        if (action.state.isContent && details != null)
            details = details.copy(
                isInWishList = false,
                isInHistory = !previousState.bookDetails!!.isInHistory
            )
        return previousState.copy(bookDetails = details) to null
    }

    //TODO
    private fun processWishListBtnPressed(
        previousState: BookDetailsScreen.ViewState,
        action: WishListBtnPressed
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var com: Command<Observable<ScreenAction>>? = null
        if (previousState.bookDetails != null) {
            if (previousState.bookDetails.isInWishList)
                com = command(
                    localRepository.deleteFromWishList(previousState.bookDetails.toBookListItemUM())
                        .doLceAction { WishListState(it) }
                )
            else
                com = command(
                    localRepository.addToWishList(previousState.bookDetails.toBookListItemUM())
                        .doLceAction { WishListState(it) }
                )
        }
        return previousState to com
    }

    private fun processWishListState(
        previousState: BookDetailsScreen.ViewState,
        action: WishListState
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var details = previousState.bookDetails
        if (action.state.isContent && details != null)
            details = details.copy(
                isInHistory = false,
                isInWishList = !previousState.bookDetails!!.isInWishList
            )
        return previousState.copy(bookDetails = details) to null
    }

    override fun createInitialState(): BookDetailsScreen.ViewState {
        return BookDetailsScreen.ViewState(
            LceState.Loading(),
            null
        )
    }

}