package ru.appkode.base.ui.books

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.MviView
import java.util.*

interface BooksMainScreen {
    interface View : MviView<ViewState> {
        fun showListIntent(): Observable<Int>
    }

    data class ViewState(val currentViewTag: Int)
}