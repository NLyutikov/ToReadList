package ru.appkode.base.ui.books.lists.search.books

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import books.details.books.BookDetailsController
import ru.appkode.base.ui.books.lists.search.ItemClicked
import ru.appkode.base.ui.books.lists.search.ScreenAction
import ru.appkode.base.ui.books.lists.search.SearchPresenter
import ru.appkode.base.ui.books.lists.search.SearchScreen
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction

class BooksSearchPresenter(
    schedulers: AppSchedulers,
    private val localRepository: BooksLocalRepository,
    private val networkRepository: BooksNetworkRepository,
    private val router: Router
) : SearchPresenter(schedulers, networkRepository, localRepository, router) {

    override fun loadContent(text: String, page: Int): Observable<List<BookListItemUM>> {
            return networkRepository.getBookSearch(text, localRepository, page)
    }

    override fun processItemClicked(
        previousState: SearchScreen.ViewState,
        action: ItemClicked
    ): Pair<SearchScreen.ViewState, Command<Observable<ScreenAction>>?> {
        return previousState to command {
            router.pushController(
                BookDetailsController.createController(
                    previousState.list[action.position].id
                ).obtainHorizontalTransaction())
        }
    }
}