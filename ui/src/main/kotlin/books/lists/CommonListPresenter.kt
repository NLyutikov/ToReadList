package ru.appkode.base.ui.books.lists

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.details.BookDetailsController
import ru.appkode.base.ui.core.core.BasePresenter
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainVerticalTransaction
import ru.appkode.base.ui.core.core.util.toLceEventObservable
import java.util.concurrent.TimeUnit

sealed class ScreenAction

data class LoadNextPageIntent(val state: LceState<List<BookListItemUM>>) : ScreenAction()
data class ItemClickedIntent(val position: Int) : ScreenAction()
data class ItemSwipedLeftIntent(val position: Int) : ScreenAction()
data class ItemSwipedRigthIntent(val position: Int) : ScreenAction()
object UpdateData : ScreenAction()
data class UpdateDataState(val state: LceState<List<BookListItemUM>>) : ScreenAction()

abstract class CommonListPresenter(
    schedulers: AppSchedulers,
    val booksLocalRepository: BooksLocalRepository,
    val booksNetworkRepository: BooksNetworkRepository,
    val router: Router
) : BasePresenter<CommonListScreen.View, CommonListScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(CommonListScreen.View::itemClickedIntent)
                .map { position -> ItemClickedIntent(position) },
            intent(CommonListScreen.View::itemSwipedLeftIntent)
                .map { position -> ItemSwipedLeftIntent(position) },
            intent(CommonListScreen.View::itemSwipedRightIntent)
                .map { position -> ItemSwipedRigthIntent(position) },
            intent(CommonListScreen.View::loadNextPageOfBooksIntent)
                .flatMap { page -> loadNextPage(page) }
                .doLceAction { lceState ->  LoadNextPageIntent(lceState)},
            intent { Observable.just(UpdateData) }
        )
    }

    override fun reduceViewState(
        previousState: CommonListScreen.ViewState,
        action: ScreenAction
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when(action) {
            is LoadNextPageIntent -> processLoadNextPage(previousState, action)
            is ItemClickedIntent -> processItemClicked(previousState, action)
            is ItemSwipedLeftIntent -> processItemSwipedLeft(previousState, action)
            is ItemSwipedRigthIntent -> processItemSwipedRight(previousState, action)
            is UpdateData -> processUpdateData(previousState, action)
            is UpdateDataState -> processUpdateDataState(previousState, action)
        }
    }

    /**
     * Возвращает observable списка книг из бд или api.
     */
    abstract fun loadNextPage(page: Int): Observable< List<BookListItemUM> >

    abstract fun updateData(numPages: Int): Observable< List<BookListItemUM> >

    protected fun processLoadNextPage(
        previousState: CommonListScreen.ViewState,
        action: LoadNextPageIntent
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var list = previousState.list
        var page = previousState.curPage
        if (action.state.isContent){
            list = list.plus(action.state.asContent())
            page += 1
        }
        return previousState.copy(
            list = list,
            curPage = page,
            loadNewPageState = action.state
        ) to null
    }

    protected fun processItemClicked(
        previousState: CommonListScreen.ViewState,
        action: ItemClickedIntent
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command { router.pushController(
            BookDetailsController.createController(previousState.list[action.position].id).obtainVerticalTransaction()
        ) }
    }

    private fun processUpdateData(
        previousState: CommonListScreen.ViewState,
        action: UpdateData
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            updateData(previousState.curPage).doLceAction { UpdateDataState(it) }
        )
    }

    private fun processUpdateDataState(
        previousState: CommonListScreen.ViewState,
        action: UpdateDataState
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var data = emptyList<BookListItemUM>()
        if (action.state.isContent)
            data = data.plus(action.state.asContent())
        return previousState.copy(
            loadNewPageState = action.state,
            list = data
        ) to null
    }

    abstract fun processItemSwipedLeft(
        previousState: CommonListScreen.ViewState,
        action: ItemSwipedLeftIntent
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?>

    abstract fun processItemSwipedRight(
        previousState: CommonListScreen.ViewState,
        action: ItemSwipedRigthIntent
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?>

    override fun createInitialState(): CommonListScreen.ViewState {
        return CommonListScreen.ViewState(1, emptyList(), LceState.Loading())
    }
}