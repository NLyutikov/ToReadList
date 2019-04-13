package ru.appkode.base.ui.books.lists.wish

import com.bluelinelabs.conductor.Router
import io.reactivex.Observable
import io.reactivex.Single
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.ui.books.lists.*
import ru.appkode.base.ui.core.core.Command
import ru.appkode.base.ui.core.core.command
import ru.appkode.base.ui.core.core.util.AppSchedulers
import kotlin.random.Random

class WishListPresenter(
    schedulers: AppSchedulers,
    booksLocalRepository: BooksLocalRepository,
    booksNetworkRepository: BooksNetworkRepository,
    router: Router
) : CommonListPresenter(schedulers, booksLocalRepository, booksNetworkRepository, router) {

    override fun loadNextPage(page: Observable<Int>): Observable<List<BookListItemUM>> {
        //return page.flatMap { booksLocalRepository.getWishList() } //FIXME реализовать получение данных по странице в бд
        //Моковые данные для проверки пагинации
        return page.subscribeOn(schedulers.io)
            .map {listOf(
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem(),
            getRandomBooksListItem()
        ) }
    }

    fun getRandomBooksListItem() = BookListItemUM(
        Random.nextLong(1, 1000),
        Random.nextInt(1, 1000).toString(),
        imagePath = "https://static.independent.co.uk/s3fs-public/thumbnails/image/2017/09/12/11/naturo-monkey-selfie.jpg?w968h681"
    )

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