package ru.appkode.base.ui.duck

import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.duck_list_controller.*
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.duck.DuckListScreen.View
import ru.appkode.base.ui.duck.DuckListScreen.ViewState
import ru.appkode.base.ui.R

class DuckListController : BaseMviController<ViewState, View, DuckListPresenter>(), View {
  override fun createConfig(): Config {
    return object : Config {
      override val viewLayoutResource: Int
        get() = R.layout.duck_list_controller
    }
  }

  private lateinit var adapter: DuckListAdapter

  override fun initializeView(rootView: android.view.View) {
    adapter = DuckListAdapter()
    duck_list_recycler.layoutManager = LinearLayoutManager(applicationContext)
    duck_list_recycler.adapter = adapter
  }

  override fun renderViewState(viewState: ViewState) {
    fieldChanged(viewState, { it.duckState }) {
      duck_list_loading.isVisible = viewState.duckState.isLoading
      duck_list_recycler.isVisible = viewState.duckState.isContent
      duck_list_empty_list.isVisible = (viewState.duckState.isContent && viewState.duckState.asContent().isEmpty())
      if (viewState.duckState.isContent) adapter.data = viewState.duckState.asContent()
    }
  }

  override fun createPresenter(): DuckListPresenter {
    return DuckListPresenter(
      DefaultAppSchedulers,
      RepositoryHelper.getDuckRepository()
    )
  }
}
