package ru.appkode.base.routing

import com.bluelinelabs.conductor.Controller
import kotlinx.android.parcel.Parcelize
import ru.appkode.base.core.model.ScreenKey

class ScreenKeyFactory: Function1<AppRoute,ScreenKey> {
    override fun invoke(route: AppRoute): ScreenKey {
        return when (route) {
            is AppRoute.List -> NotImplemented
            is AppRoute.Info -> NotImplemented
        }
    }
}

@Parcelize
private object NotImplemented : ScreenKey(){
    override fun createController(): Controller {
        return Controller()
    }
}
