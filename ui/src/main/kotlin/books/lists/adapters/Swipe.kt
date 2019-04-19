package books.lists.adapters

import android.annotation.SuppressLint
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemAdapter
import com.h6ah4i.android.widget.advrecyclerview.swipeable.SwipeableItemConstants
import com.h6ah4i.android.widget.advrecyclerview.swipeable.action.SwipeResultAction
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
//            SwipeableItemConstants.RESULT_SWIPED_RIGHT -> getSwipeAction {
//                delegateControlsAdapter().eventRelay.accept(EVENT_ID_ITEM_SWIPED_RIGHT to position)
//            }
//            SwipeableItemConstants.RESULT_SWIPED_LEFT -> getSwipeAction {
//                delegateControlsAdapter().eventRelay.accept(EVENT_ID_ITEM_SWIPED_LEFT to position)
//            }
            else -> null
        }

    override fun onGetSwipeReactionType(
        holder: CommonListAdapter.ViewHolder,
        position: Int, x: Int, y: Int
    ): Int = SwipeableItemConstants.REACTION_CAN_SWIPE_BOTH_H


    @SuppressLint("SwitchIntDef")
    override fun onSetSwipeBackground(holder: CommonListAdapter.ViewHolder, position: Int, type: Int) {
    }
}

const val EVENT_ID_ITEM_SWIPED_LEFT = 7
const val EVENT_ID_ITEM_SWIPED_RIGHT = 8
