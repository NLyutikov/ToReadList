package ru.appkode.base.ui.books.lists

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jakewharton.rxbinding3.recyclerview.scrollEvents
import io.reactivex.Observable
import kotlinx.android.synthetic.main.books_list_controller.*
import ru.appkode.base.entities.core.books.lists.BookListItemUM
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
        initRecyclerView()
        initSwipes()
        initDragAndDrop()
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
    }

    /**
     * FIXME При паджинации подвисает
     */
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

    override fun itemSwipedLeftIntent(): Observable<Int> {
        return Observable.just(1) //TODO должени возвращать Observable< Позиция свайпнутого элемента >
    }

    override fun itemSwipedRightIntent(): Observable<Int> {
        return Observable.just(1) //TODO должени возвращать Observable< Позиция свайпнутого элемента >
    }
}

val COMMON_LIST_EVENT_PAGE_LOADED = 300