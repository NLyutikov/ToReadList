package ru.appkode.base.ui.books.details

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.book_details_controller.*
import kotlinx.android.synthetic.main.duck_list_controller.*
import kotlinx.android.synthetic.main.network_error.*
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.setVisibilityAndText
import ru.appkode.base.ui.task.change.ChangeTaskDescription
import timber.log.Timber

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
        book_details_toolbar.setNavigationOnClickListener { router.handleBack() }
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
            book_details_scroll_content_view.isVisible = viewState.bookDetailsState.isContent
            network_error_screen_container.isVisible = viewState.bookDetailsState.isError
            if (viewState.bookDetailsState.isContent)
                showContent(viewState)
        }
    }

    private fun showContent(viewState: BookDetailsScreen.ViewState) {
        val book = viewState.bookDetailsState.asContent()
        with(book) {
            showHeader(title, coverImageUrl)
            showSmallRatingAndNumPages(averageRating, ratingsCount, pagesNumber)
            showDescription(description)
            showSimilarBooks(similarBooks)
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

    private fun showHeader(title: String?, imageCoverUrl: String?) {
        book_details_title_container.isVisible = !title.isNullOrBlank() || !imageCoverUrl.isNullOrBlank()

        book_details_title_text.setVisibilityAndText(title)
        Picasso.Builder(applicationContext!!).build()
            .load(imageCoverUrl)
            .error(R.drawable.test_img)
            .into(book_details_cover_image)

        //TODO Author and genres
    }

    override fun showSimilarBookIntent(): Observable<Long> {
        return similarBooksAdapter.itemClicked
    }

    override fun createPresenter(): BookDetailsPresenter {
        return BookDetailsPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            this.router!!,
            bookId
        )
    }
}

private const val ARG_BOOK_ID = "book_id"