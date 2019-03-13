package ru.appkode.base.ui.core.core.routing

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.IdRes
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.RouterTransaction
import ru.appkode.base.ui.core.core.model.ScreenKey
import ru.appkode.base.ui.core.core.routing.transition.RouterTransitionType
import ru.appkode.base.ui.core.core.util.*
import ru.appkode.ui.core.BuildConfig
import timber.log.Timber

// Нет смысла разбивать на подклассы, т.к. это роутер, и для него нормально имплементировать столько функций
@Suppress("TooManyFunctions")
class ConductorAppRouter<RouteType : Route> constructor(
    private val screenKeyFactory: (RouteType) -> ScreenKey
) : Router<RouteType> {

    private lateinit var conductorRouter: com.bluelinelabs.conductor.Router
    private val routeObservers: MutableList<RouteObserver> = mutableListOf()
    /**
     * A mapping from controller instanceId to its route path
     */
    private val controllerRoutes: MutableMap<String, String> = mutableMapOf()

    // (!) Must only be called by hosting activity
    fun attachTo(
        host: Activity,
        @IdRes containerViewId: Int,
        savedInstanceState: Bundle?,
        initialRoute: RouteType? = null,
        initialTransitionType: RouterTransitionType? = null
    ) {
        val container = host.findViewById<ViewGroup>(containerViewId)
        conductorRouter = Conductor.attachRouter(host, container, savedInstanceState)
        conductorRouter.addChangeListener(ChangeListener())
        if (!conductorRouter.hasRootController() && initialRoute != null) {
            val transitionType = initialTransitionType
                ?: RouterTransitionType.None
            push(initialRoute, transitionType).invoke()
        }
    }

    // (!) Must only be called by hosting activity
    fun handleBack(): Boolean {
        return conductorRouter.handleBack()
    }

    // (!) Must only be called by hosting activity
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        conductorRouter.onActivityResult(requestCode, resultCode, data)
    }

    override fun addRouteObserver(observer: RouteObserver) {
        routeObservers.add(observer)
    }

    override fun removeRouteObserver(observer: RouteObserver) {
        routeObservers.remove(observer)
    }

    override fun push(route: RouteType, transitionType: RouterTransitionType?): RouteCommand {
        return route.toCommandSafe(conductorRouter.backstack) {
            val transaction = createTransaction(it, transitionType).addToControllerRoutes()
            conductorRouter.pushController(transaction)
        }
    }

    override fun pushReplacement(route: RouteType, transitionType: RouterTransitionType?): RouteCommand {
        return route.toCommandSafe(conductorRouter.backstack) {
            val transaction = createTransaction(it, transitionType).addToControllerRoutes()
            conductorRouter.replaceTopController(transaction)
        }
    }

    override fun pushAndRemoveUntil(
        route: RouteType,
        transitionType: RouterTransitionType?,
        predicate: (String) -> Boolean
    ): RouteCommand {

        val transaction = createTransaction(route, transitionType)
        val newBackstack = conductorRouter.backstack
            .dropLastWhile { val path = it.tag()!!; !predicate(path) }
            .plus(transaction)

        return route.toCommandSafe(newBackstack.dropLast(1)) {
            transaction.addToControllerRoutes()
            conductorRouter.setBackstack(newBackstack, transaction.pushChangeHandler())
            // see NOTE_ROUTE_MAPPING_CLEAN_UP
        }
    }

    override fun pop(): RouteCommand {
        return {
            conductorRouter.popCurrentController()
            // see NOTE_ROUTE_MAPPING_CLEAN_UP
        }
    }

    override fun popTo(route: RouteType): RouteCommand {
        return {
            val newBackstack = conductorRouter.backstack.dropLastWhile { it.tag() != route.path }
            if (newBackstack.isNotEmpty()) {
                conductorRouter.setBackstack(newBackstack, createDefaultHorizontalChangeHandler(isPush = false))
                // see NOTE_ROUTE_MAPPING_CLEAN_UP
            } else {
                Timber.e("failed to popTo(${route.javaClass.simpleName}): no such route found")
            }
        }
    }

    private fun createTransaction(route: RouteType, transitionType: RouterTransitionType?): RouterTransaction {
        val controller = screenKeyFactory(route).newController()
        return transitionType.toControllerTransaction(controller)
            // have to use 'path' here rather than route,
            // because conductor will save/restore only 'String' based tags on rotation
            .tag(route.path)
    }

    private fun RouterTransaction.addToControllerRoutes(): RouterTransaction {
        controllerRoutes[controller().instanceId] = tag()!!
        return this
    }

    private inner class ChangeListener : ControllerChangeHandler.ControllerChangeListener {
        override fun onChangeStarted(
            to: Controller?,
            from: Controller?,
            isPush: Boolean,
            container: ViewGroup,
            handler: ControllerChangeHandler
        ) {
            for (observer in routeObservers) {
                val toPath = if (to != null) controllerRoutes[to.instanceId] else null
                if (toPath == null) {
                    Timber.d("'to' controller is null, not reporting change to observers")
                    continue
                }
                val fromPath = if (from != null) controllerRoutes[from.instanceId] else null
                if (isPush) {
                    observer.pushStarted(toPath, fromPath)
                } else {
                    observer.popStarted(toPath, fromPath)
                }
            }
        }

        override fun onChangeCompleted(
            to: Controller?,
            from: Controller?,
            isPush: Boolean,
            container: ViewGroup,
            handler: ControllerChangeHandler
        ) {
            for (observer in routeObservers) {
                val toPath = if (to != null) controllerRoutes[to.instanceId] else null
                if (toPath == null) {
                    Timber.d("'to' controller is null, not reporting change to observers")
                    continue
                }
                val fromPath = if (from != null) controllerRoutes[from.instanceId] else null
                if (isPush) {
                    observer.pushCompleted(toPath, fromPath)
                } else {
                    observer.popCompleted(toPath, fromPath)
                }
                // see NOTE_ROUTE_MAPPING_CLEAN_UP
                cleanupOldRoutes()
            }
        }

        private fun cleanupOldRoutes() {
            val sz = controllerRoutes.size
            controllerRoutes.clear()
            for (t in conductorRouter.backstack) {
                controllerRoutes[t.controller().instanceId] = t.tag()!!
            }
            if (sz - controllerRoutes.size > 0) {
                Timber.d("cleaned up ${sz - controllerRoutes.size} old routes")
            }
        }
    }
}

private fun RouterTransitionType?.toControllerTransaction(controller: Controller): RouterTransaction {
    val type = this ?: RouterTransitionType.Horizontal
    return when (type) {
        is RouterTransitionType.Horizontal -> controller.obtainHorizontalTransaction()
        is RouterTransitionType.Vertical -> controller.obtainVerticalTransaction()
        is RouterTransitionType.Fade -> controller.obtainFadeTransaction()
        is RouterTransitionType.None -> RouterTransaction.with(controller)
        is RouterTransitionType.Shared -> controller.obtainSharedTransaction(type.transition)
    }
}

/**
 * Проверяет, нет ли нового роута в конце бэкстека, если есть, то новая команда никуда не ведет.
 * Нужно, чтобы исключить ситуации, когда в конец стека случайно попадают одинаковые роуты.
 * Например, такое может произойти при быстрых кликах по кнопкам.
 */
private fun <RouteType : Route> RouteType.toCommandSafe(
    backstack: List<RouterTransaction>,
    safeRoutingCommand: (RouteType) -> Unit
): RouteCommand {
    return {
        val newRoute = this
        if (backstack.lastOrNull()?.tag() != newRoute.path) {
            if (BuildConfig.DEBUG) {
                checkRoutePathUniqueness(newRoute, backstack)
            }
            safeRoutingCommand.invoke(newRoute)
        } else {
            Timber.e("tried add same route $newRoute, just ignore it")
        }
    }
}

/**
 * Checks that newly added route has a unique path.
 * This Router implementation relies on route path uniqueness in several methods, for example [ConductorAppRouter.popTo]
 */
private fun checkRoutePathUniqueness(newRoute: Route, backstack: List<RouterTransaction>) {
    check(backstack.none { it.tag() == newRoute.path }) {
        val position = backstack.indexOfFirst { it.tag() == newRoute.path }
        "Route path must be unique, backstack contains duplicate path at position $position: ${newRoute.path}"
    }
}

// NOTE_ROUTE_MAPPING_CLEAN_UP
// Mappings from controller instances to their route paths are kept until transition completes,
// i.e. during a transition route mappings can contain instances of controllers which are not
// in backstack anymore. This is done to simplify implementation of routing observers to not
// keep track of from/to controller route mappings when pop is in progress or vice versa
