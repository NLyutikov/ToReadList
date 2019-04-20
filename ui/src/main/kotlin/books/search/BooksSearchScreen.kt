package ru.appkode.base.ui.books.search

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface BooksSearchScreen {
    interface View : MviView<ViewState> {
        fun searchBookIntent(): Observable<String>
        fun showImageIntent(): Observable<String>
        fun dismissImageIntent(): Observable<Unit>
        fun repeatSearchIntent(): Observable<Unit>
        fun itemClickedIntent(): Observable<Int>
        fun loadPageIntent(): Observable<Pair<String, Int>>
    }

    data class ViewState(
        val booksSearchState: LceState<List<BookListItemUM>>,
        val url: String?,
        val query: String?,
        val page: Int,
        val list: List<BookListItemUM>
    )
}