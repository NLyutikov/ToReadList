package ru.appkode.base.ui.routing

import ru.appkode.base.ui.core.core.model.ScreenKey
import ru.appkode.base.ui.task.list.TaskListKey

//TODO Убрать nullable у ScreenKey
class ScreenKeyFactory : Function1<AppRoute, ScreenKey?> {
    override fun invoke(route: AppRoute): ScreenKey? {
        return when (route) {
            is AppRoute.List -> TaskListKey()
            is AppRoute.Info -> null
        }
    }
}
