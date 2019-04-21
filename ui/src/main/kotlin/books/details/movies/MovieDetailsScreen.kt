package ru.appkode.base.ui.books.details.movies

import ru.appkode.base.entities.core.movies.details.MovieDetailsUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface MovieDetailsScreen {
    interface View : MviView<ViewState> {

    }

    data class ViewState(val movieDetailsState: LceState<MovieDetailsUM>)
}