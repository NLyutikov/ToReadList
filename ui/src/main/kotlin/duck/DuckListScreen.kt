package ru.appkode.base.ui.duck

import ru.appkode.base.entities.core.duck.DuckUM
import ru.appkode.base.ui.core.core.LceState
import ru.appkode.base.ui.core.core.MviView

interface DuckListScreen {
  interface View : MviView<ViewState>

  data class ViewState(
    val duckState: LceState<List<DuckUM>>
  )
}
