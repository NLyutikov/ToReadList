package ru.appkode.base.routing

import ru.appkode.base.core.routing.Route

sealed class AppRoute(override val path: String) : Route {
    object List :AppRoute("list")
    data class Info(val itemId: String) : AppRoute("list/item/$itemId")
}