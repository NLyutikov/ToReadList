package ru.appkode.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.appkode.base.ui.core.core.BaseActivity
import ru.appkode.base.ui.core.core.routing.ConductorAppRouter
import ru.appkode.base.ui.core.core.routing.Route
import ru.appkode.base.ui.routing.AppRoute
import ru.appkode.base.ui.routing.ScreenKeyFactory

class MainActivity : BaseActivity() {

    override var router = ConductorAppRouter<Route> { ScreenKeyFactory().invoke(it as AppRoute) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        router.attachTo(this, R.id.main_container, savedInstanceState, AppRoute.List)
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}
