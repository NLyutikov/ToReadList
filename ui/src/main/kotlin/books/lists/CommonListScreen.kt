package ru.appkode.base.ui.books.lists

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.ui.books.lists.adapters.DropItemInfo
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface CommonListScreen {
    interface View : MviView<ViewState> {
        fun loadNextPageOfBooksIntent(): Observable<Int>
        fun itemClickedIntent(): Observable<Int>
        fun itemSwipedLeftIntent(): Observable<Int>
        fun itemSwipedRightIntent(): Observable<Int>
        fun refreshIntent(): Observable<Unit>
        fun historyIconClickedIntent(): Observable<Int>
        fun wishListIconClickedIntent(): Observable<Int>
        fun deleteIconClickedIntent(): Observable<Int>
        fun itemDroppedIntent(): Observable<DropItemInfo>
        fun showImageIntent(): Observable<String>
        fun dismissImageIntent(): Observable<Unit>
    }

    data class ViewState(
        val curPage: Int,
        val list: List<BookListItemUM>,
        val loadNewPageState: LceState<List<BookListItemUM>>,
        val isRefreshing: Boolean,
        val url: String?
    )
}