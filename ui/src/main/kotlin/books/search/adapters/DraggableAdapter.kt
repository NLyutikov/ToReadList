package ru.appkode.base.ui.books.search.adapters

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_item.view.*
import ru.appkode.base.entities.core.books.search.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.books.search.BooksSearchAdapter
import ru.appkode.base.ui.core.core.util.filterEvents
import java.util.*


internal class DraggableAdapter() : ListActions,
    RecyclerView.Adapter<DraggableAdapter.ViewHolder>(),
    DraggableItemAdapter<DraggableAdapter.ViewHolder> {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            Picasso.get().load(data[position].imgPath).into(bookImg)
            bookName.text = data[position].title
            bookRating.text = data[position].averageRating.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var data: LinkedList<BookUM> = emptyList<BookUM>() as LinkedList<BookUM>
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    init {
        // DraggableItemAdapter requires stable ID, and also
        // have to implement the getItemId() method appropriately.
        setHasStableIds(true)
    }

    private val eventsRelay: PublishRelay<Pair<Int, Any>> = PublishRelay.create<Pair<Int, Any>>()
    val imageClicked: Observable<String> = eventsRelay.filterEvents(EVENT_ID_IMAGE_CLICKED)

    override fun count(): Int = data.size

    //возвращает книгу по индексу
    override fun getItem(index: Int): BookUM {

        if (index < 0 || index >= count()) {
            throw IndexOutOfBoundsException("index = $index")
        }

        return data[index]
    }

    //изменяет положение элемента (FROM_POS,TO_POS)
    override fun changeItemPosition(fromPosition: Int, toPosition: Int) {

        if (fromPosition == toPosition) {
            return
        }

        val item = data.removeAt(fromPosition)

        data.add(toPosition, item)
    }

    override fun getItemCount(): Int {
        return count()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val bookImg: ImageView = view.books_list_item_image
        val bookName: TextView = view.books_list_item_name
        val bookRating: TextView = view.books_list_item_rating_text
        val bookCard:ConstraintLayout = view.book_card

        init {
            bookImg.setOnClickListener {
                eventsRelay.accept(ru.appkode.base.ui.books.search.EVENT_ID_IMAGE_CLICKED to data[adapterPosition].imgPath!!)
            }
        }
    }

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {
        Log.d(TAG, "onMoveItem(fromPosition = $fromPosition, toPosition = $toPosition)")
        changeItemPosition(fromPosition, toPosition)
    }

    override fun onCheckCanStartDrag(holder: ViewHolder, position: Int, x: Int, y: Int): Boolean {
        // x, y --- relative from the itemView's top-left
        val containerView = holder.bookCard
        //TODO(исправить, пока двигать будем за картинку)
        val dragHandleView = holder.bookImg

        val offsetX = containerView.left + (containerView.translationX + 0.5f).toInt()
        val offsetY = containerView.top + (containerView.translationY + 0.5f).toInt()

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY)
    }

    override fun onGetItemDraggableRange(holder: ViewHolder, position: Int): ItemDraggableRange? {
        // no drag-sortable range specified
        return null
    }

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean {
        return true
    }

    override fun onItemDragStarted(position: Int) {
        notifyDataSetChanged()
    }

    override fun onItemDragFinished(fromPosition: Int, toPosition: Int, result: Boolean) {
        notifyDataSetChanged()
    }

    companion object {
        private val TAG = "MyDraggableItemAdapter"
    }
}

object DrawableUtils {
    private val EMPTY_STATE = intArrayOf()

    fun clearState(drawable: Drawable?) {
        if (drawable != null) {
            drawable.state = EMPTY_STATE
        }
    }
}

object ViewUtils {
    fun hitTest(v: View, x: Int, y: Int): Boolean {
        val tx = (v.translationX + 0.5f).toInt()
        val ty = (v.translationY + 0.5f).toInt()
        val left = v.left + tx
        val right = v.right + tx
        val top = v.top + ty
        val bottom = v.bottom + ty

        return x >= left && x <= right && y >= top && y <= bottom
    }

}

private const val EVENT_ID_IMAGE_CLICKED = 3