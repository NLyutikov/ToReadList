package ru.appkode.base.ui.books.details

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface BookDetailsScreen {

    interface View : MviView<ViewState> {
        fun showSimilarBookIntent(): Observable<Long>
        fun showMoreInfoIntent(): Observable<Unit>
        fun historyBtnPressed(): Observable<Unit>
        fun wishListBtnPressed(): Observable<Unit>
    }

    data class ViewState(
        val bookDetailsState: LceState<BookDetailsUM>,
        val bookDetails: BookDetailsUM? = null
    )
}