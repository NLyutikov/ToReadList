package ru.appkode.base.ui.books.search

import io.reactivex.Observable
import ru.appkode.base.entities.core.books.search.SearchResultUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface BooksSearchScreen {
    interface View : MviView<ViewState> {
        fun searchBookIntent(): Observable<String>
        fun showImageIntent(): Observable<String>
        fun dismissImageIntent(): Observable<Unit>
    }

    data class ViewState(
        val booksSearchState: LceState<List<SearchResultUM>>,
        val url: String?
    )
}