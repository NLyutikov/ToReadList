package ru.appkode.base.ui.books.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_item.view.*
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.repository.books.BooksLocalRepositoryImpl
import ru.appkode.base.repository.books.IMAGE_DIR
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents
import java.io.File
import java.util.*

class CommonListAdapter(var fromLocalDataSource: Boolean = false) : RecyclerView.Adapter<CommonListAdapter.ViewHolder>() {

    var data = emptyList<BookListItemUM>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val eventRelay: PublishRelay< Pair<Int, Int> > = PublishRelay.create()

    val itemClicked: Observable<Int> = eventRelay.filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.books_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            if (fromLocalDataSource)
                Picasso.get()
                    .load(
                        File(
                            view.context.getDir(IMAGE_DIR, Context.MODE_PRIVATE),
                            BooksLocalRepositoryImpl.getImageNameById(data[position].id)
                        )
                    ).into(image)
            else
                Picasso.get().load(data[position].imagePath).into(image)
            title.text = data[position].title
            rating.text = data[position].averageRating.toString()
            wishListIcon.isVisible = data[position].isInWishList!!
            historyIcon.isVisible = data[position].isInHistory!!
        }
    }

    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.books_list_item_image
        val title = view.books_list_item_name
        val rating = view.books_list_item_rating_text
        val wishListIcon = view.books_list_item_favorites
        val historyIcon = view.books_list_item_visible

        init {
            view.setOnClickListener {
                eventRelay.accept(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED to adapterPosition)
            }
        }
    }
}

const val COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED = 12