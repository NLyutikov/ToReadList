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

sealed class ScreenAction

data class LoadNextPage(val state: LceState<List<BookListItemUM>>) : ScreenAction()
data class ItemClicked(val position: Int) : ScreenAction()
data class ItemSwipedLeft(val position: Int) : ScreenAction()
data class ItemSwipedRight(val position: Int) : ScreenAction()
object UpdateData : ScreenAction()
data class UpdateDataState(val state: LceState<List<BookListItemUM>>) : ScreenAction()
object Refreshing : ScreenAction()
data class RefreshingState(val state: LceState<List<BookListItemUM>>) : ScreenAction()

abstract class CommonListPresenter(
    schedulers: AppSchedulers,
    val booksLocalRepository: BooksLocalRepository,
    val booksNetworkRepository: BooksNetworkRepository,
    val router: Router
) : BasePresenter<CommonListScreen.View, CommonListScreen.ViewState, ScreenAction>(schedulers) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(CommonListScreen.View::itemClickedIntent)
                .map { position -> ItemClicked(position) },
            intent(CommonListScreen.View::itemSwipedLeftIntent)
                .map { position -> ItemSwipedLeft(position) },
            intent(CommonListScreen.View::itemSwipedRightIntent)
                .map { position -> ItemSwipedRight(position) },
            intent(CommonListScreen.View::loadNextPageOfBooksIntent)
                .flatMap { page -> loadNextPage(page) }
                .doLceAction { lceState ->  LoadNextPage(lceState)},
            intent(CommonListScreen.View::refreshIntent)
                .map { Refreshing },
            intent { Observable.just(UpdateData) }
        )
    }

    override fun reduceViewState(
        previousState: CommonListScreen.ViewState,
        action: ScreenAction
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return when(action) {
            is LoadNextPage -> processLoadNextPage(previousState, action)
            is ItemClicked -> processItemClicked(previousState, action)
            is ItemSwipedLeft -> processItemSwipedLeft(previousState, action)
            is ItemSwipedRight -> processItemSwipedRight(previousState, action)
            is UpdateData -> processUpdateData(previousState)
            is UpdateDataState -> processUpdateDataState(previousState, action)
            is Refreshing -> processRefreshing(previousState)
            is RefreshingState -> processRefreshingState(previousState, action)
        }
    }

    /**
     * Возвращает observable списка книг из бд или api.
     */
    abstract fun loadNextPage(page: Int): Observable< List<BookListItemUM> >

    abstract fun updateData(numPages: Int): Observable< List<BookListItemUM> >

    protected fun processLoadNextPage(
        previousState: CommonListScreen.ViewState,
        action: LoadNextPage
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var list = previousState.list
        var page = previousState.curPage
        if (action.state.isContent) {
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
        action: ItemClicked
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command { router.pushController(
            BookDetailsController.createController(previousState.list[action.position].id).obtainVerticalTransaction()
        ) }
    }

    protected fun processUpdateData(
        previousState: CommonListScreen.ViewState
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            updateData(previousState.curPage).doLceAction { UpdateDataState(it) }
        )
    }

    protected fun processUpdateDataState(
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

    protected fun processRefreshing(
        previousState: CommonListScreen.ViewState
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState.copy(
            curPage = 1,
            isRefreshing = true
        ) to command(
            loadNextPage(1).doLceAction { lceState ->  RefreshingState(lceState) }
        )
    }

    protected fun processRefreshingState(
        previousState: CommonListScreen.ViewState,
        action: RefreshingState
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        var list = previousState.list
        var page = previousState.curPage
        var isRefreshing = previousState.isRefreshing
        if (action.state.isContent) {
            list = action.state.asContent()
            page = 1
            isRefreshing = false
        }
        return previousState.copy(
            list = list,
            curPage = page,
            loadNewPageState = action.state,
            isRefreshing = isRefreshing
        ) to null
    }

    abstract fun processItemSwipedLeft(
        previousState: CommonListScreen.ViewState,
        action: ItemSwipedLeft
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?>

    abstract fun processItemSwipedRight(
        previousState: CommonListScreen.ViewState,
        action: ItemSwipedRight
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?>

    override fun createInitialState(): CommonListScreen.ViewState {
        return CommonListScreen.ViewState(1, emptyList(), LceState.Loading(), false)
    }
}