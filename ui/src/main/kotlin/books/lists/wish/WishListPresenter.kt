package ru.appkode.base.ui.books.lists.wish

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.lists.*
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers

class WishListPresenter(
    schedulers: AppSchedulers,
    booksLocalRepository: BooksLocalRepository,
    booksNetworkRepository: BooksNetworkRepository,
    router: Router
) : CommonListPresenter(schedulers, booksLocalRepository, booksNetworkRepository, router) {

    override fun createIntents(): List<Observable<out ScreenAction>> {
        return listOf(
            intent(CommonListScreen.View::historyIconClickedIntent)
                .map { AddToHistory(it) },
            intent(CommonListScreen.View::deleteIconClickedIntent)
                .map { DeleteFromWishList(it) },
            intent(CommonListScreen.View::itemDroppedIntent)
                .map { ItemDropped(it) }
        ).plus(super.createIntents())
    }

    override fun loadNextPage(page: Int): Observable<List<BookListItemUM>> {
        return booksLocalRepository.getWishListPage(page)
    }

    override fun updateData(numPages: Int): Observable<List<BookListItemUM>> {
        return booksLocalRepository.getFirstWishListPages(numPages)
    }

    override fun bindItemSwipedLeft(): Observable<out ScreenAction> {
        return intent(CommonListScreen.View::itemSwipedLeftIntent).map { DeleteFromWishList(it) }
    }

    override fun bindItemSwipedRight(): Observable<out ScreenAction> {
        return intent(CommonListScreen.View::itemSwipedRightIntent).map { AddToHistory(it) }
    }

    override fun processAddToHistory(
        previousState: CommonListScreen.ViewState,
        action: AddToHistory
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            booksLocalRepository.addToHistoryFromWishList(previousState.list[action.position])
                .doAction { ChangeList { list -> list.minus(previousState.list[action.position]) } }
        )
    }

    override fun processDeleteFromWishList(
        previousState: CommonListScreen.ViewState,
        action: DeleteFromWishList
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command(
            booksLocalRepository.deleteFromWishList(previousState.list[action.position])
                .doAction { ChangeList { list -> list.minus(previousState.list[action.position]) } }
        )
    }

    override fun processAddToWishList(
        previousState: CommonListScreen.ViewState,
        action: AddToWishList
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to null
    }

    override fun processDeleteFromHistory(
        previousState: CommonListScreen.ViewState,
        action: DeleteFromHistory
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to null
    }

}