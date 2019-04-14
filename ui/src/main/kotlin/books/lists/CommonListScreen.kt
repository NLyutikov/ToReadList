package ru.appkode.base.ui.books.lists

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface CommonListScreen {
    interface View : MviView<ViewState> {
        fun loadNextPageOfBooksIntent(): Observable<Int>
        fun itemClickedIntent(): Observable<Int>
        fun itemSwipedLeftIntent(): Observable<Int>
        fun itemSwipedRightIntent(): Observable<Int>
    }

    data class ViewState(
        val curPage: Int,
        val list: List<BookListItemUM>,
        val loadNewPageState: LceState<List<BookListItemUM>>
    )
}