package ru.appkode.base.ui.books.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.book_details_similar_books_list_item.view.*
import ru.appkode.base.entities.core.books.details.BookDetailsUM
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents

class SimilarBooksListAdapter : RecyclerView.Adapter<SimilarBooksListAdapter.ViewHolder>() {

    var data: List<BookDetailsUM> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val eventsRelay = PublishRelay.create<Pair<Int, Long>>()

    val itemClicked = eventsRelay.filterEvents(EVENT_ID_ITEM_CLICKED)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.book_details_similar_books_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            title.text = data[position].title
            Picasso.get()
                .load(data[position].coverImageUrl)
                .into(image)
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.similar_books_list_image_cover
        val title = view.similar_books_list_title_title

        init {
            view.setOnClickListener {
                eventsRelay.accept(EVENT_ID_ITEM_CLICKED to data[adapterPosition].id)
            }
        }
    }
}

private const val EVENT_ID_ITEM_CLICKED = 0