package ru.appkode.base.ui.books

import ru.appkode.base.ui.core.core.MviView

class BooksMainScreen {
    interface View : MviView<ViewState> {
        fun wishListIntent()
        fun historyListIntent()
    }

    data class ViewState(val currentViewId: Int)
}