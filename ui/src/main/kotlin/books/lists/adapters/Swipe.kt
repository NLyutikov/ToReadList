package books.lists.adapters

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionDoNothing
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultActionRemoveItem
import ru.appkode.base.ui.R
import ru.appkode.base.ui.books.lists.adapters.CommonListAdapter

interface Swipe : SwipeableItemAdapter<CommonListAdapter.ViewHolder> {
    fun delegateControlsAdapter(): CommonListAdapter

    override fun onSwipeItemStarted(holder: CommonListAdapter.ViewHolder, position: Int) {
        delegateControlsAdapter().notifyDataSetChanged()
    }

    @SuppressLint("SwitchIntDef")
    override fun onSwipeItem(
        holder: CommonListAdapter.ViewHolder,
        position: Int, result: Int
    ): SwipeResultAction? =
        when (result) {
            SwipeableItemConstants.RESULT_SWIPED_RIGHT -> getSwipeAction {
                delegateControlsAdapter().eventsRelay.accept(EVENT_ID_ITEM_SWIPED_RIGHT to position)
            }
            SwipeableItemConstants.RESULT_SWIPED_LEFT -> getSwipeAction {
                delegateControlsAdapter().eventsRelay.accept(EVENT_ID_ITEM_SWIPED_LEFT to position)
            }
            else -> null
        }

    fun getSwipeAction(action: () -> Unit): SwipeResultAction

    override fun onGetSwipeReactionType(
        holder: CommonListAdapter.ViewHolder,
        position: Int, x: Int, y: Int
    ): Int = SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H


    @SuppressLint("SwitchIntDef")
    override fun onSetSwipeBackground(holder: CommonListAdapter.ViewHolder, position: Int, type: Int) {
        var bgRes = 0
        if (holder.wishListIcon.isVisible) {
            when (type) {
                SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND ->
                    bgRes = R.drawable.wish_list_bg_swipe_item_left
                SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND ->
                    bgRes = R.drawable.wish_list_bg_swipe_item_right
            }
        }
        if (holder.historyIcon.isVisible) {
            when (type) {
                SwipeableItemConstants.DRAWABLE_SWIPE_LEFT_BACKGROUND ->
                    bgRes = R.drawable.history_bg_swipe_item_left
                SwipeableItemConstants.DRAWABLE_SWIPE_RIGHT_BACKGROUND ->
                    bgRes = R.drawable.history_bg_swipe_item_right
            }
        }
        holder.itemView.setBackgroundResource(bgRes)
    }
}

object SwipeActions {

    class Remove(private val action: () -> Unit) : SwipeResultActionRemoveItem() {
        override fun onPerformAction() = action.invoke()
    }

    class DoNothing(private val action: () -> Unit) : SwipeResultActionDoNothing() {
        override fun onPerformAction() = action.invoke()
    }

}

const val EVENT_ID_ITEM_SWIPED_LEFT = 7
const val EVENT_ID_ITEM_SWIPED_RIGHT = 8
