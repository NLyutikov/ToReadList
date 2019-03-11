package ru.appkode.base.ui.core.core.routing

import ru.appkode.base.ui.core.core.routing.transition.RouterTransitionType

typealias RouteCommand = () -> Unit

interface Router<RouteType : Route> {
    fun push(route: RouteType, transitionType: RouterTransitionType? = null): RouteCommand
    fun pushReplacement(route: RouteType, transitionType: RouterTransitionType? = null): RouteCommand
    /**
     * Push the given route onto the router and then remove all the previous routes until the [predicate] returns true.
     *
     * @param predicate receives a route path and returns true if this is the route
     * on top of which push should be performed
     */
    fun pushAndRemoveUntil(
        route: RouteType,
        transitionType: RouterTransitionType? = null,
        predicate: (String) -> Boolean
    ): RouteCommand

    fun pop(): RouteCommand
    fun popTo(route: RouteType): RouteCommand

    fun addRouteObserver(observer: RouteObserver)
    fun removeRouteObserver(observer: RouteObserver)
}

interface Route {
    val path: String
}

fun <RouteType : Route> Router<RouteType>.setRoot(
    route: RouteType,
    transitionType: RouterTransitionType? = null
): RouteCommand {
    return pushAndRemoveUntil(route, transitionType) { false }
}
