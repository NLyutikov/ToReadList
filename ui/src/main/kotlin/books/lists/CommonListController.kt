package ru.appkode.base.ui.books.lists

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
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

    abstract protected val listAdapter: CommonListAdapter

    override fun initializeView(rootView: View) {
        initSwipeRefresh()
        initRecyclerView()
        initSwipes()
        initDragAndDrop()
    }

    protected fun initSwipeRefresh() {
        books_list_swipe_refresh.setOnRefreshListener {
            eventsRelay.accept(COMMON_LIST_REFRESH_EVENT_ID to Unit)
        }
    }

    protected fun initRecyclerView() = with (books_list_recycler) {
        layoutManager = LinearLayoutManager(applicationContext!!)
        adapter = listAdapter
    }

    protected fun initDragAndDrop() {
        //TODO написать иницилизацию перетаскиваний
    }

    protected fun initSwipes() {
        //TODO написать иницилизацию свайпов
    }

    override fun renderViewState(viewState: CommonListScreen.ViewState) {
        fieldChanged(viewState, {state ->  state.loadNewPageState}) {
            with(viewState) {
                books_list_loading.isVisible = loadNewPageState.isLoading && list.isEmpty()
                books_list_empty_list.isVisible =
                     loadNewPageState.isContent && loadNewPageState.asContent().isEmpty() && list.isEmpty()
            }
        }

        fieldChanged(viewState, {state ->  state.list}) {
            with(viewState) {
                books_list_recycler.isVisible = list.isNotEmpty()
                listAdapter.data = list
            }
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
            }.throttleFirst( 500, TimeUnit.MILLISECONDS)
            .map { previousViewState!!.curPage + 1 }
    }

    override fun itemClickedIntent(): Observable<Int> {
        return listAdapter.itemClicked
            .throttleFirst(500, TimeUnit.MILLISECONDS)
    }

    override fun refreshIntent(): Observable<Unit> {
        return eventsRelay.filterEvents(COMMON_LIST_REFRESH_EVENT_ID)
    }

    override fun itemSwipedLeftIntent(): Observable<Int> {
        return Observable.just(1) //TODO должени возвращать Observable< Позиция свайпнутого элемента >
    }

    override fun itemSwipedRightIntent(): Observable<Int> {
        return Observable.just(1) //TODO должени возвращать Observable< Позиция свайпнутого элемента >
    }

    override fun historyIconClickedIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun wishListIconCickedIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteIconClickedIntent(): Observable<Int> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

const val COMMON_LIST_REFRESH_EVENT_ID = 31