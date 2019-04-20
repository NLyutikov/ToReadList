package ru.appkode.base.ui.books.details

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.book_details_controller.*
import kotlinx.android.synthetic.main.network_error.*
import ru.appkode.base.entities.core.books.details.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.core.core.util.setVisibilityAndText
import java.util.concurrent.TimeUnit

class BookDetailsController :
    BaseMviController<BookDetailsScreen.ViewState, BookDetailsScreen.View, BookDetailsPresenter>(),
    BookDetailsScreen.View {

    companion object {
        fun createController(bookId: Long): BookDetailsController {
            return BookDetailsController().apply {
                args.putLong(ARG_BOOK_ID, bookId)
            }
        }
    }

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.book_details_controller
        }
    }

    private val bookId: Long by lazy {args.getLong(ARG_BOOK_ID)}

    private lateinit var similarBooksAdapter: SimilarBooksListAdapter

    override fun initializeView(rootView: View) {
        book_details_back_btn.setOnClickListener { router.handleBack() }

        //TODO
        book_details_add_to_want_to_read_btn.setOnClickListener {
            Snackbar.make(rootView, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
        }

        book_details_about_book_container.setOnClickListener {
            Snackbar.make(rootView, "Not implemented yet", Snackbar.LENGTH_SHORT).show()
            eventsRelay.accept(EVENT_ID_MORE_INFO to Unit)
        }

        similarBooksAdapter = SimilarBooksListAdapter()
        book_details_similar_books_list.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        book_details_similar_books_list.adapter = similarBooksAdapter
    }

    override fun renderViewState(viewState: BookDetailsScreen.ViewState) {
        fieldChanged(viewState, {viewState -> viewState.bookDetailsState }) {
            book_details_load_progress_bar.isVisible = viewState.bookDetailsState.isLoading
            book_details_content_view.isVisible = viewState.bookDetailsState.isContent
            network_error_screen_container.isVisible = viewState.bookDetailsState.isError
            if(viewState.bookDetailsState.isContent)
                showContent(viewState)
        }

        fieldChanged(viewState, {viewState ->  viewState.bookDetails?.isInHistory ?: 1}) {
            if (viewState.bookDetails != null)
                showHistoryAndWishListIcons(viewState.bookDetails.isInHistory, viewState.bookDetails.isInWishList)
        }

        fieldChanged(viewState, {viewState ->  viewState.bookDetails?.isInWishList ?: 1}) {
            if (viewState.bookDetails != null)
                showHistoryAndWishListIcons(viewState.bookDetails.isInHistory, viewState.bookDetails.isInWishList)
        }
    }

    private fun showContent(viewState: BookDetailsScreen.ViewState) {
        val book = viewState.bookDetails
        check(book != null)
        with(book) {
            showHeader(title, shelves, authors, coverImageUrl)
            showSmallRatingAndNumPages(averageRating, ratingsCount, pagesNumber)
            showDescription(description)
            showSimilarBooks(similarBooks)
            showHistoryAndWishListIcons(isInHistory, isInWishList)
        }
    }

    private fun showSmallRatingAndNumPages(rating: Double?, ratingCount: Int?, numPages: Int?) {
        book_details_rating_pages_container.isVisible = rating != null && numPages != null
        //rating
        book_details_small_rating_text.setVisibilityAndText(rating?.toString())
        book_details_small_rating_num_reviews_text.setVisibilityAndText(ratingCount?.toString())

        //pages
        book_details_num_pages_text.setVisibilityAndText(numPages?.toString())
    }

    private fun showSimilarBooks(books: List<BookDetailsUM>?) {
        book_details_similar_books_container.isVisible = !books.isNullOrEmpty()
        similarBooksAdapter.data = books ?: emptyList()
    }

    private fun showDescription(description: String?) {
        book_details_about_book_container.isVisible = !description.isNullOrBlank()
        book_details_description_text.setVisibilityAndText(description)
    }

    private fun showHeader(title: String?, shelves: List<ShelfUM>?, authors: List<AuthorUM>?, imageCoverUrl: String?) {
        book_details_title_container.isVisible = !title.isNullOrBlank() || !imageCoverUrl.isNullOrBlank()

        //Title
        book_details_title_text.setVisibilityAndText(title)
        //Authors
        book_details_author_text.setVisibilityAndText(authors?.toAuthorsLine(0..2))
        //Genres
        book_details_genres_text.setVisibilityAndText(shelves?.toShelvesLine(0..2))
        //Cover Image
        Glide.with(applicationContext!!)
            .load(imageCoverUrl)
            .error(R.drawable.without_cover_png)
            .into(book_details_cover_image)
    }

    private fun showHistoryAndWishListIcons(isInHistory: Boolean, isInWishList: Boolean) {
        book_details_add_to_want_to_read_btn.isVisible = isInWishList || !isInHistory && !isInWishList
        book_details_add_to_history_btn.isVisible = isInHistory || !isInHistory && !isInWishList

        when {
            !isInHistory && !isInWishList -> {
                book_details_add_to_want_to_read_btn.setImageResource(R.drawable.outline_turned_in_not_24)
                book_details_add_to_history_btn.setImageResource(R.drawable.ic_history_24dp)
            }
            isInHistory -> {
                book_details_add_to_history_btn.setImageResource(R.drawable.ic_history_blue_24dp)
            }
            isInWishList -> {
                book_details_add_to_want_to_read_btn.setImageResource(R.drawable.outline_turned_in_24)
            }
        }
    }

    override fun reloadBookDetails(): Observable<Unit> {
        return network_error_screen_reload_btn.clicks()
    }

    override fun showSimilarBookIntent(): Observable<Long> {
        return similarBooksAdapter.itemClicked
    }

    override fun showMoreInfoIntent(): Observable<Unit> {
        return eventsRelay.filterEvents(EVENT_ID_MORE_INFO)
    }

    override fun historyBtnPressed(): Observable<Unit> {
        return book_details_add_to_history_btn.clicks().throttleFirst(100, TimeUnit.MILLISECONDS)
    }

    override fun wishListBtnPressed(): Observable<Unit> {
        return book_details_add_to_want_to_read_btn.clicks().throttleFirst(100, TimeUnit.MILLISECONDS)
    }

    override fun createPresenter(): BookDetailsPresenter {
        return BookDetailsPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            this.router!!,
            bookId
        )
    }
}

private const val ARG_BOOK_ID = "book_id"

private const val EVENT_ID_MORE_INFO = 100