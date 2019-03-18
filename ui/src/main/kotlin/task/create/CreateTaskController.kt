package ru.appkode.base.ui.task.create

import androidx.core.widget.doOnTextChanged
import io.reactivex.Observable
import kotlinx.android.synthetic.main.create_task_controller.*
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.filterEvents
import ru.appkode.base.ui.task.create.CreateTaskScreen.View
import ru.appkode.base.ui.task.create.CreateTaskScreen.ViewState

class CreateTaskController : BaseMviController<ViewState, View, CreateTaskPresenter>(), View {
  override fun createConfig(): Config {
    return object : Config {
      override val viewLayoutResource = R.layout.create_task_controller
    }
  }

  override fun initializeView(rootView: android.view.View) {
    create_task_toolbar.setNavigationOnClickListener { router.handleBack() }
    create_task_title.doOnTextChanged { text, _, _, _ ->
      eventsRelay.accept(EVENT_ID_TITLE_CHANGED to text.toString())
    }
    create_task_description.doOnTextChanged { text, _, _, _ ->
      eventsRelay.accept(EVENT_ID_DESCRIPTION_CHANGED to text.toString())
    }
  }

  override fun renderViewState(viewState: ViewState) {
    create_task_toolbar.title = viewState.task.title

    create_task_title.setText(viewState.task.title)
    create_task_title.setSelection(viewState.task.title.length)

    create_task_description.setText(viewState.task.description)
    create_task_description.setSelection(viewState.task.description.length)
  }

  override fun changeTaskTitleIntent(): Observable<String> {
    return eventsRelay.filterEvents(EVENT_ID_TITLE_CHANGED)
  }

  override fun changeTaskDescription(): Observable<String> {
    return eventsRelay.filterEvents(EVENT_ID_DESCRIPTION_CHANGED)
  }

  override fun createPresenter(): CreateTaskPresenter {
    return CreateTaskPresenter(DefaultAppSchedulers)
  }
}

private const val EVENT_ID_TITLE_CHANGED = 0
private const val EVENT_ID_DESCRIPTION_CHANGED = 1
