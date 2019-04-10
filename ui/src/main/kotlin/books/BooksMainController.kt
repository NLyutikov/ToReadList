package ru.appkode.base.ui.books

import android.view.View
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class BooksMainController :
    BaseMviController<BooksMainScreen.ViewState, BooksMainScreen.View, BooksMainPresenter>(),
    BooksMainScreen.View {

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.book_details_controller
        }
    }

    override fun initializeView(rootView: View) {


    }

    override fun renderViewState(viewState: BooksMainScreen.ViewState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wishListIntent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun historyListIntent() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createPresenter(): BooksMainPresenter {
        return BooksMainPresenter(DefaultAppSchedulers)
    }

}