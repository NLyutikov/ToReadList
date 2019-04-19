package ru.appkode.base.ui.books.lists.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableSwipeableItemViewHolder
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_item.view.*
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents

abstract class CommonListAdapter(
    private val fromLocalDataSource: Boolean = false,
    private val draggable: Boolean = false
) : RecyclerView.Adapter<CommonListAdapter.ViewHolder>() {

    var data = emptyList<BookListItemUM>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    val eventRelay: PublishRelay< Pair<Int, Any> > = PublishRelay.create()

    val itemClicked: Observable<Int> = eventRelay.filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED)

    val wishListIconClicked: Observable<Int> = eventRelay
        .filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_WISH_LIST_ICON_CLICKED)

    val historyIconClicked: Observable<Int> = eventRelay
        .filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_HISTORY_ICON_CLICKED)

    val deleteIconClicked: Observable<Int> = eventRelay
        .filterEvents(COMMON_LIST_ADAPTER_EVENT_ID_DELETE_ICON_CLICKED)

    val itemDropped: Observable<DropItemInfo> =
        eventRelay.filterEvents<Pair<Int, Int>>(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_DROPPED)
            .flatMap {
                Observable.just(getDropItemInfo(it.first, it.second))
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.books_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Glide.with(itemView.context)
                .load(data[position].imagePath)
                .onlyRetrieveFromCache(fromLocalDataSource)
                .into(image)
            title.text = data[position].title
            rating.text = data[position].averageRating.toString()
            wishListIcon.isVisible = !data[position].isInWishList
            historyIcon.isVisible = !data[position].isInHistory
            dragIcon.isVisible = draggable
        }
    }

    override fun getItemCount(): Int = data.size

    override fun getItemId(position: Int): Long = data[position].id

    fun getDropItemInfo(
        from: Int,
        to: Int
    ): DropItemInfo {
        val item = data[from]
        var top: BookListItemUM? = null
        var bottom: BookListItemUM? = null
        when {
            to > 0 && to < data.size - 1 -> {
                when {
                    to - from < 0 -> {
                        top = data[to - 1]
                        bottom = data[to]
                    }
                    to - from > 0 -> {
                        top = data[to]
                        bottom = data[to + 1]
                    }
                    to - from == 0 -> {
                        top = data[to - 1]
                        bottom = data[to + 1]
                    }
                }
            }
            to == 0 -> bottom = data[to]
            to == data.size - 1 -> top = data[to]
        }
        return DropItemInfo(from, to, item, bottom, top)
    }

    inner class ViewHolder(view: View) : AbstractDraggableSwipeableItemViewHolder(view) {
        val container = itemView.book_list_item_container
        val image = itemView.books_list_item_image
        val title = itemView.books_list_item_name
        val rating = itemView.books_list_item_rating_text
        val wishListIcon = itemView.books_list_item_wish_list
        val historyIcon = itemView.books_list_item_history
        val deleteIcon = itemView.books_list_item_delete
        val dragIcon = itemView.books_list_item_drag_img

        init {
            view.setOnClickListener { view ->
                    if (view !is ImageView)
                        eventRelay.accept(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED to adapterPosition)
            }
            wishListIcon.setOnClickListener {
                eventRelay.accept(
                    COMMON_LIST_ADAPTER_EVENT_ID_WISH_LIST_ICON_CLICKED to adapterPosition
                )
            }
            historyIcon.setOnClickListener {
                eventRelay.accept(
                    COMMON_LIST_ADAPTER_EVENT_ID_HISTORY_ICON_CLICKED to adapterPosition
                )
            }
            deleteIcon.setOnClickListener {
                eventRelay.accept(
                    COMMON_LIST_ADAPTER_EVENT_ID_DELETE_ICON_CLICKED to adapterPosition
                )
            }
        }

        override fun getSwipeableContainerView(): View = container
    }
}

const val COMMON_LIST_ADAPTER_EVENT_ID_ITEM_CLICKED = 12
const val COMMON_LIST_ADAPTER_EVENT_ID_WISH_LIST_ICON_CLICKED = 13
const val COMMON_LIST_ADAPTER_EVENT_ID_HISTORY_ICON_CLICKED = 14
const val COMMON_LIST_ADAPTER_EVENT_ID_DELETE_ICON_CLICKED = 15
const val COMMON_LIST_ADAPTER_EVENT_ID_ITEM_DROPPED = 16