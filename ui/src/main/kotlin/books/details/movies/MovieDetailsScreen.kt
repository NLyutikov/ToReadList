package ru.appkode.base.ui.books.details.movies

import io.reactivex.Observable
import ru.appkode.base.entities.core.movies.details.MovieDetailsUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface MovieDetailsScreen {
    interface View : MviView<ViewState> {
        fun wishListBtnPressedIntent(): Observable<Unit>
        fun historyBtnPressedIntent(): Observable<Unit>
        fun showMovieTrailerIntent(): Observable<String?>
    }

    data class ViewState(
        val movieDetailsState: LceState<MovieDetailsUM>,
        val details: MovieDetailsUM?
    )
}