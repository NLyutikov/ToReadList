package ru.appkode.base.ui.books.details

import android.app.ProgressDialog.show
import com.bluelinelabs.conductor.Router
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction

sealed class ScreenAction

data class LoadBookDetails(val state: LceState<BookDetailsUM>) : ScreenAction()
data class ShowSimilarBook(val bookId: Long) : ScreenAction()
object ShowMoreInformation : ScreenAction()

class BookDetailsPresenter(
    schedulers: AppSchedulers,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router,
    private val bookId: Long
) : BasePresenter<BookDetailsScreen.View, BookDetailsScreen.ViewState, ScreenAction>(schedulers){

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(BookDetailsScreen.View::showMoreInfoIntent)
                .map{ShowMoreInformation},
            intent(BookDetailsScreen.View::showSimilarBookIntent)
                .map{ShowSimilarBook(it)},
            intent { networkRepository.getBookDetails(bookId) }
                .doLceAction { LoadBookDetails(it) }//TODO add error check
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
        }
    }

    override fun createInitialState(): BookDetailsScreen.ViewState {
        return BookDetailsScreen.ViewState(
            LceState.Loading()
        )
    }

    private fun processLoadBookDetails(
        previousState: BookDetailsScreen.ViewState,
        action: LoadBookDetails
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            bookDetailsState = action.state
        ) to null
    }

    private fun processShowSimilarBook(
        previousState: BookDetailsScreen.ViewState,
        action: ShowSimilarBook
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>> {
        return previousState to command {
            router.pushController(
                BookDetailsController.createController(action.bookId).obtainHorizontalTransaction()
            )
        }
    }

    private fun processShowMoreInformation(
        previousState: BookDetailsScreen.ViewState,
        action: ShowMoreInformation
    ) : Pair<BookDetailsScreen.ViewState, Command<Observable<ScreenAction>>> {
        return previousState to command {
            //TODO показать доп. информацию о книге
        }
    }

}