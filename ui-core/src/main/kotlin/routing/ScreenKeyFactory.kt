package ru.appkode.base.ui.core.routing

import ru.appkode.base.ui.core.core.model.ScreenKey

//TODO Убрать nullable у ScreenKey
class ScreenKeyFactory : Function1<AppRoute, ScreenKey?> {
    override fun invoke(route: AppRoute): ScreenKey? {
        return when (route) {
            is AppRoute.List -> null
            is AppRoute.Info -> null
        }
    }
}
