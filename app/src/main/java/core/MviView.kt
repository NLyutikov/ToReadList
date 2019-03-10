package ru.appkode.base.core

import com.hannesdorfmann.mosby3.mvp.MvpView

interface MviView<in VS> : MvpView {
  fun render(viewState: VS)
}
