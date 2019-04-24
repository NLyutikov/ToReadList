package ru.appkode.base.ui.books.lists

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import books.lists.adapters.EVENT_ID_ITEM_SWIPED_LEFT
import books.lists.adapters.EVENT_ID_ITEM_SWIPED_RIGHT
import com.bumptech.glide.Glide
import com.h6ah4i.android.widget.advrecyclerview.animator.DraggableItemAnimator
import com.h6ah4i.android.widget.advrecyclerview.draggable.RecyclerViewDragDropManager
import com.h6ah4i.android.widget.advrecyclerview.swipeable.RecyclerViewSwipeManager
import com.h6ah4i.android.widget.advrecyclerview.touchguard.RecyclerViewTouchActionGuardManager
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import com.stfalcon.imageviewer.StfalconImageViewer
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.books.lists.adapters.CommonListAdapter
import ru.appkode.base.ui.books.lists.adapters.DropItemInfo
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.eventThrottleFirst
import ru.appkode.base.ui.core.core.util.filterEvents
import java.util.concurrent.TimeUnit

abstract class CommonListController :
    BaseMviController<CommonListScreen.ViewState, CommonListScreen.View, CommonListPresenter>(),
    CommonListScreen.View {

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.books_list_controller
        }
    }

    protected lateinit var listAdapter: CommonListAdapter

    abstract fun getBooksListAdapter(): CommonListAdapter

    override fun initializeView(rootView: View) {
        initSwipeRefresh()
        initRecyclerView()
    }

    protected fun initSwipeRefresh() {
        books_list_swipe_refresh.setOnRefreshListener {
            eventsRelay.accept(COMMON_LIST_REFRESH_EVENT_ID to Unit)
        }
    }

    protected fun initRecyclerView() = with (books_list_recycler) {
        listAdapter = getBooksListAdapter()
        if (!listAdapter.hasStableIds())
            listAdapter.setHasStableIds(true)

        val actionGuardManager = RecyclerViewTouchActionGuardManager()
        actionGuardManager.setInterceptVerticalScrollingWhileAnimationRunning(true)
        actionGuardManager.isEnabled = true

        val dragDropManager = RecyclerViewDragDropManager()
        val dragDropAdapter = dragDropManager.createWrappedAdapter(listAdapter)
        val swipeManager = RecyclerViewSwipeManager()
        val swipeDragAdapter =
            swipeManager.createWrappedAdapter(dragDropAdapter)

        val animator = DraggableItemAnimator()
        animator.supportsChangeAnimations = false

        books_list_recycler.adapter = swipeDragAdapter
        books_list_recycler.itemAnimator = animator
        books_list_recycler.layoutManager = LinearLayoutManager(applicationContext)

        actionGuardManager.attachRecyclerView(books_list_recycler)
        dragDropManager.attachRecyclerView(books_list_recycler)
        swipeManager.attachRecyclerView(books_list_recycler)
    }

    override fun renderViewState(viewState: CommonListScreen.ViewState) {
        fieldChanged(viewState, {state ->  state.loadNewPageState}) {
            with(viewState) {
                books_list_loading.isVisible = loadNewPageState.isLoading && list.isEmpty()
                books_list_empty_list.isVisible = loadNewPageState.isContent && list.isEmpty()
            }
        }

        fieldChanged(viewState, {state ->  state.list}) {
            with(viewState) {
                books_list_recycler.isVisible = list.isNotEmpty()
                books_list_empty_list.isVisible = loadNewPageState.isContent && list.isEmpty()
                listAdapter.data = list
            }
        }

        fieldChanged(viewState, { it.url.orEmpty() }) {
            if (viewState.url.isNullOrBlank()) return@fieldChanged
            StfalconImageViewer.Builder<String>(activity, listOf(viewState.url)) { view, url ->
                Glide.with(applicationContext!!).load(url).into(view)
            }
                .withDismissListener { eventsRelay.accept(EVENT_ID_IMAGE_DISMISS to Unit) }
                .show()
        }

        fieldChanged(viewState, {state -> state.isRefreshing}) {
            books_list_swipe_refresh.isRefreshing = viewState.isRefreshing
        }
    }

    override fun loadNextPageOfBooksIntent(): Observable<Int> {
        return books_list_recycler.scrollEvents()
            .filter {
                val manager = books_list_recycler.layoutManager as LinearLayoutManager
                val totalItemCount = manager.itemCount
                val visibleItemCount = manager.childCount
                val firstVisibleItem = manager.findFirstVisibleItemPosition()
                val isLoading: Boolean? = previousViewState?.loadNewPageState?.isLoading
                val nextPage: Int? = previousViewState?.curPage

                val limit = if (totalItemCount >= 60) totalItemCount - 20 else totalItemCount / 2

                return@filter nextPage != null &&
                        isLoading != null &&
                        !isLoading  &&
                        firstVisibleItem + visibleItemCount >= limit
            }.eventThrottleFirst()
            .map { previousViewState!!.curPage + 1 }
    }

    override fun itemClickedIntent(): Observable<Int> {
        return listAdapter.itemClicked
            .eventThrottleFirst()
    }

    override fun refreshIntent(): Observable<Unit> {
        return eventsRelay.filterEvents<Unit>(COMMON_LIST_REFRESH_EVENT_ID)
            .eventThrottleFirst()
    }

    override fun itemSwipedLeftIntent(): Observable<Int> {
        return listAdapter.eventsRelay.filterEvents<Int>(EVENT_ID_ITEM_SWIPED_LEFT)
    }

    override fun itemSwipedRightIntent(): Observable<Int> {
        return listAdapter.eventsRelay.filterEvents<Int>(EVENT_ID_ITEM_SWIPED_RIGHT)
    }

    override fun itemDroppedIntent(): Observable<DropItemInfo> {
        return listAdapter.itemDropped
    }

    override fun showImageIntent(): Observable<String> {
        return listAdapter.imageClicked
    }

    override fun dismissImageIntent(): Observable<Unit> {
        return eventsRelay.filterEvents(EVENT_ID_IMAGE_DISMISS)
    }

    override fun historyIconClickedIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wishListIconClickedIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteIconClickedIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

const val COMMON_LIST_REFRESH_EVENT_ID = 31
const val EVENT_ID_IMAGE_DISMISS = 1