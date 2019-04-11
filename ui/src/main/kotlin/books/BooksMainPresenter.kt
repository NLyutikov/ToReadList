package ru.appkode.base.ui.books

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.util.AppSchedulers

sealed class ScreenAction
data class ShowList(val viewTag: Int) : ScreenAction()

class BooksMainPresenter(
    schedulers: AppSchedulers,
    val router: Router
) : BasePresenter<BooksMainScreen.View, BooksMainScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(BooksMainScreen.View::showListIntent)
                .map { itemId -> ShowList(itemId) }
        )
    }

    override fun reduceViewState(
        previousState: BooksMainScreen.ViewState,
        action: ScreenAction
    ): Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when(action) {
            is ShowList -> processShowList(previousState, action)
        }
    }

    private fun processShowList(
        previousState: BooksMainScreen.ViewState,
        action: ShowList
    ) : Pair<BooksMainScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(currentViewTag = action.viewTag) to null
    }

    override fun createInitialState(): BooksMainScreen.ViewState {
        return BooksMainScreen.ViewState(VIEW_TAG_1)
    }
}