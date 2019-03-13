package ru.appkode.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.appkode.base.ui.core.core.routing.ConductorAppRouter
import ru.appkode.base.ui.core.core.routing.Route
import ru.appkode.base.ui.routing.AppRoute
import ru.appkode.base.ui.routing.ScreenKeyFactory

class MainActivity : AppCompatActivity() {

    private lateinit var router: ConductorAppRouter<Route>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        router = ConductorAppRouter { ScreenKeyFactory().invoke(it as AppRoute) }
        router.attachTo(this, R.id.main_container, savedInstanceState, AppRoute.List)
    }
}
