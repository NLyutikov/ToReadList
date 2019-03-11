package ru.appkode.base.ui.core.core.routing

interface RouteObserver {
  fun pushStarted(routePath: String, previousRoutePath: String?)
  fun popStarted(routePath: String, previousRoutePath: String?)
  fun pushCompleted(routePath: String, previousRoutePath: String?)
  fun popCompleted(routePath: String, previousRoutePath: String?)
}
