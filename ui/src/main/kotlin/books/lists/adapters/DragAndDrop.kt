package ru.appkode.base.ui.books.lists.adapters

import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange
import kotlinx.android.synthetic.main.books_list_item.view.*
import ru.appkode.base.entities.core.books.lists.BookListItemUM
import ru.appkode.base.ui.books.utils.UiUtils

interface DragAndDrop : DraggableItemAdapter<CommonListAdapter.ViewHolder> {

    fun adapter(): CommonListAdapter

    override fun onGetItemDraggableRange(
        holder: CommonListAdapter.ViewHolder,
        position: Int
    ): ItemDraggableRange? = null

    override fun onItemDragStarted(position: Int) = adapter().notifyDataSetChanged()

    override fun onCheckCanDrop(draggingPosition: Int, dropPosition: Int): Boolean = true

    override fun onMoveItem(fromPosition: Int, toPosition: Int) {}

    override fun onItemDragFinished(start: Int, end: Int, result: Boolean) {
        if (result)
            adapter().eventsRelay.accept(COMMON_LIST_ADAPTER_EVENT_ID_ITEM_DROPPED to Pair<Int, Int>(start, end))
    }

    override fun onCheckCanStartDrag(holder: CommonListAdapter.ViewHolder, position: Int, x: Int, y: Int): Boolean {
        val offsetX = holder.itemView.book_list_item_container.left +
                (holder.itemView.book_list_item_container.translationX + 0.5f).toInt()

        val offsetY = holder.itemView.book_list_item_container.top +
                (holder.itemView.book_list_item_container.translationY + 0.5f).toInt()

        return UiUtils.hitTest(holder.itemView.books_list_item_drag_img, x - offsetX, y - offsetY)
    }

}

data class DropItemInfo(
    val from: Int,
    val newPos: Int,
    val item: BookListItemUM,
    val left: BookListItemUM?,
    val right: BookListItemUM?
)
