package ru.appkode.base.ui.books.details.movies

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.movies.details.MovieDetailsUM
import ru.appkode.base.entities.core.movies.details.toBookListUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction

data class LoadMovieState(val state: LceState<MovieDetailsUM>) : ScreenAction()
object WishListBtnPressed : ScreenAction()
object HistoryBtnPressed : ScreenAction()
data class WishListBtnPressedState(val state: LceState<Unit>) : ScreenAction()
data class HistoryBtnPressedState(val state: LceState<Unit>) : ScreenAction()

class MovieDetailsPresenter(
    schedulers: AppSchedulers,
    private val localRepository: BooksLocalRepository,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router,
    private val movieId: Long
) : BasePresenter<MovieDetailsScreen.View, MovieDetailsScreen.ViewState, ScreenAction>(schedulers) {


    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent {
                networkRepository.getMovieDetails(movieId, localRepository)
                    .doLceAction { LoadMovieState(it) }
            },
            intent(MovieDetailsScreen.View::historyBtnPressedIntent)
                .map { HistoryBtnPressed },
            intent(MovieDetailsScreen.View::wishListBtnPressedIntent)
                .map { WishListBtnPressed }
        )
    }


    override fun reduceViewState(
        previousState: MovieDetailsScreen.ViewState,
        action: ScreenAction
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when (action) {
            is LoadMovieState -> processLoadMovieState(previousState, action)
            is WishListBtnPressed -> processWishListBtnPressed(previousState)
            is HistoryBtnPressed -> processHistoryBtnPressed(previousState)
            is WishListBtnPressedState -> processWishListBtnPressedState(previousState, action)
            is HistoryBtnPressedState -> processHistoryBtnPressedState(previousState, action)
        }
    }

    private fun processWishListBtnPressed(
        previousState: MovieDetailsScreen.ViewState
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var com: Command<Observable<ScreenAction>>? = null
        if (previousState.details != null)
            if (!previousState.details.isInWishList) {
                com = command(
                    localRepository.addToWishList(previousState.details.toBookListUM())
                        .doLceAction { WishListBtnPressedState(it) }
                )
            } else {
                com = command(
                    localRepository.deleteFromWishList(previousState.details.toBookListUM())
                        .doLceAction { WishListBtnPressedState(it) }
                )
            }
        return previousState to com
    }

    private fun processHistoryBtnPressed(
        previousState: MovieDetailsScreen.ViewState
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var com: Command<Observable<ScreenAction>>? = null
        if (previousState.details != null)
            if (!previousState.details.isInHistory) {
                com = command(
                    localRepository.addToHistory(previousState.details.toBookListUM())
                        .doLceAction { HistoryBtnPressedState(it) }
                )
            } else {
                com = command(
                    localRepository.deleteFromHistory(previousState.details.toBookListUM())
                        .doLceAction { HistoryBtnPressedState(it) }
                )
            }
        return previousState to com
    }

    private fun processWishListBtnPressedState(
        previousState: MovieDetailsScreen.ViewState,
        action: WishListBtnPressedState
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var details = previousState.details
        if(action.state.isContent && details != null)
            details = details.copy(
                isInHistory = false,
                isInWishList = !details.isInWishList
            )
        return previousState.copy(details = details) to null
    }

    private fun processHistoryBtnPressedState(
        previousState: MovieDetailsScreen.ViewState,
        action: HistoryBtnPressedState
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var details = previousState.details
        if(action.state.isContent && details != null)
            details = details.copy(
                isInWishList = false,
                isInHistory = !details.isInHistory
            )
        return previousState.copy(details = details) to null
    }
    private fun processLoadMovieState(
        previousState: MovieDetailsScreen.ViewState,
        action: LoadMovieState
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
       return previousState.copy(
           movieDetailsState = action.state,
           details = if (action.state.isContent) action.state.asContent() else null
       ) to null
    }

    override fun createInitialState(): MovieDetailsScreen.ViewState {
        return MovieDetailsScreen.ViewState(
            movieDetailsState = LceState.Loading(),
            details = null
        )
    }
}