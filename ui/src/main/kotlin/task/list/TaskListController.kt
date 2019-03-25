package ru.appkode.base.ui.task.list

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.task_list_controller.*
import ru.appkode.base.repository.task.TaskRepositoryImpl
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.task.list.TaskListScreen.View
import ru.appkode.base.ui.task.list.TaskListScreen.ViewState

class TaskListController : BaseMviController<ViewState, View, TaskListPresenter>(), View {
  override fun createConfig(): Config {
    return object : Config {
      override val viewLayoutResource: Int
        get() = R.layout.task_list_controller
    }
  }

  private lateinit var adapter: TaskListAdapter

  override fun initializeView(rootView: android.view.View) {
    adapter = TaskListAdapter()
    task_list_recycler.layoutManager = LinearLayoutManager(applicationContext)
    task_list_recycler.adapter = adapter
  }

  override fun renderViewState(viewState: ViewState) {
    if (fieldChanged(viewState) { it.taskState }) {
      task_list_loading.isVisible = viewState.taskState.isLoading
      task_list_recycler.isVisible = viewState.taskState.isContent
      if (viewState.taskState.isContent) adapter.data = viewState.taskState.asContent()
    }
  }

  override fun switchTaskIntent(): Observable<Long> {
    return adapter.itemClicked
  }

  override fun createTaskIntent(): Observable<Unit> {
    return task_list_create.clicks()
  }

  override fun createPresenter(): TaskListPresenter {
    return TaskListPresenter(DefaultAppSchedulers, TaskRepositoryImpl(DefaultAppSchedulers), this.router!!)
  }
}
