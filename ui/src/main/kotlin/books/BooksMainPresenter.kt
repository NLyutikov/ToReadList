package ru.appkode.base.ui.books

import io.reactivex.Observable
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction

class BooksMainPresenter(
    schedulers: AppSchedulers
) : BasePresenter<BooksMainScreen.View, BooksMainScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun reduceViewState(
        previousState: BooksMainScreen.ViewState,
        action: ScreenAction
    ): Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createInitialState(): BooksMainScreen.ViewState {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}