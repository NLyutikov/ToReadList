package ru.appkode.base.ui.books

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView
import java.util.*

interface BooksMainScreen {
    interface View : MviView<ViewState> {
        fun showListIntent(): Observable<String>
        fun showSearchList(): Observable<Unit>
        fun showBookSearchList(): Observable<Unit>
        fun showMovieSearchList(): Observable<Unit>
        fun dialogCanceled(): Observable<Unit>
    }

    data class ViewState(
        val currentViewTag: String,
        val showDialog: Boolean
    )
}