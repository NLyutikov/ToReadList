package ru.appkode.base.ui.core.core

import androidx.appcompat.app.AppCompatActivity
import ru.appkode.base.ui.core.core.routing.Route
import ru.appkode.base.ui.core.core.routing.Router

abstract class BaseActivity: AppCompatActivity() {
    abstract val router: Router<Route>
}