package ru.appkode.base.ui.books.lists.history

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.lists.*
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import kotlin.random.Random

class HistoryPresenter(
    schedulers: AppSchedulers,
    booksLocalRepository: BooksLocalRepository,
    booksNetworkRepository: BooksNetworkRepository,
    router: Router
) : CommonListPresenter(schedulers, booksLocalRepository, booksNetworkRepository, router) {

    override fun loadNextPage(page: Int): Observable<List<BookListItemUM>> {
        return booksLocalRepository.getHistoryPage(page)
    }

    override fun updateData(numPages: Int): Observable<List<BookListItemUM>> {
        return booksLocalRepository.getFirstHistoryPages(numPages)
    }

    override fun processItemSwipedLeft(
        previousState: CommonListScreen.ViewState,
        action: ItemSwipedLeftIntent
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command {  } //TODO релизовать свайпы
    }

    override fun processItemSwipedRight(
        previousState: CommonListScreen.ViewState,
        action: ItemSwipedRigthIntent
    ): Pair<CommonListScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command {  } //TODO релизовать свайпы
    }
}