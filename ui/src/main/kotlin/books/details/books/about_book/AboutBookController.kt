package ru.appkode.base.ui.books.details.books.about_book

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bluelinelabs.conductor.Controller
import kotlinx.android.synthetic.main.book_details_about_book.view.*
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.entities.core.books.details.toAuthorsLine
import ru.appkode.base.ui.R

class AboutBookController : Controller() {

    companion object {
        fun createController(book: BookDetailsUM?): AboutBookController {
            return AboutBookController().apply {
                args.putParcelable(ABOUT_BOOK_ARGS_BOOK, book)
            }
        }
    }

    lateinit var book: BookDetailsUM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.book_details_about_book, container, false)

        view.about_book_back_arrow.setOnClickListener { router.handleBack() }

        book = args.getParcelable(ABOUT_BOOK_ARGS_BOOK) ?: BookDetailsUM(-1)
        showContent(view)

        return view
    }

    private fun showContent(view: View) = with(view) {
        about_book_title.text = book.title

        //Description
        about_book_description_container.isVisible = book.description != null
        about_book_description_text.text = book.description

        //isbn
        about_book_isbn_container.isVisible = book.isbn13 != null
        about_book_isbn_text.text = book.isbn13

        //Language
        about_book_language_container.isVisible = !book.language.isNullOrBlank()
        about_book_language_text.text = book.language

        //Pages Number
        about_book_page_num_container.isVisible = book.pagesNumber != null
        about_book_page_num_text.text = book.pagesNumber?.toString()

        //Authors
        about_book_authors_container.isVisible = !book.authors.isNullOrEmpty()
        about_book_authors_text.text = book.authors?.toAuthorsLine()
    }

}

private const val ABOUT_BOOK_ARGS_BOOK = "144"