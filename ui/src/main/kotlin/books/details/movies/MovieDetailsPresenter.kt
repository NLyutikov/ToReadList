package ru.appkode.base.ui.books.details.movies

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction

class MovieDetailsPresenter(
    schedulers: AppSchedulers,
    private val localRepository: BooksLocalRepository,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router
) : BasePresenter<MovieDetailsScreen.View, MovieDetailsScreen.ViewState, ScreenAction>(schedulers) {


    override fun createIntents(): List<Observable<out ScreenAction>> {
        return emptyList()
    }


    override fun reduceViewState(
        previousState: MovieDetailsScreen.ViewState,
        action: ScreenAction
    ): Pair<MovieDetailsScreen.ViewState, Command<Observable<ScreenAction>>?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createInitialState(): MovieDetailsScreen.ViewState {
        return MovieDetailsScreen.ViewState(movieDetailsState = LceState.Loading())
    }
}