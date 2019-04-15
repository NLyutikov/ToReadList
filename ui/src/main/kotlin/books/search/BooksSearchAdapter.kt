package ru.appkode.base.ui.books.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_item.view.*
import ru.appkode.base.entities.core.books.search.BookUM
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents

class BooksSearchAdapter : RecyclerView.Adapter<BooksSearchAdapter.ViewHolder>() {

    var data: List<BookUM> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()
    val imageClicked: Observable<String> = eventsRelay.filterEvents(EVENT_ID_IMAGE_CLICKED)
    val itemClicked = eventsRelay.filterEvents<Long>(EVENT_ID_ITEM_CLICKED)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.books_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: BooksSearchAdapter.ViewHolder, position: Int) {
        with(holder) {
            Picasso.get().load(data[position].imgPath).into(bookImg)
            bookName.text = data[position].title
            bookRating.text = data[position].averageRating.toString()
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImg: ImageView = view.books_list_item_image
        val bookName: TextView = view.books_list_item_name
        val bookRating: TextView = view.books_list_item_rating_text

        init {
            bookImg.setOnClickListener {
                eventsRelay.accept(EVENT_ID_IMAGE_CLICKED to data[adapterPosition].imgPath!!)
            }
            view.setOnClickListener {v ->
                if (v !is ImageView)
                    eventsRelay.accept(EVENT_ID_ITEM_CLICKED to data[adapterPosition].id!!)
            }
        }
    }
}

const val EVENT_ID_IMAGE_CLICKED = 3
const val EVENT_ID_ITEM_CLICKED = 4
