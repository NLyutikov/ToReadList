package ru.appkode.base.ui.books

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView
import java.util.*

interface BooksMainScreen {
    interface View : MviView<ViewState> {
        fun showListIntent(): Observable<String>
        fun showSearchList(): Observable<Unit>
        fun showBookSearchListIntent(): Observable<Unit>
        fun showMovieSearchListIntent(): Observable<Unit>
        fun dialogCanceledIntent(): Observable<Unit>
    }

    data class ViewState(
        val currentViewTag: String,
        val showDialog: Boolean
    )
}