package ru.appkode.base.core.routing.transition

sealed class RouterTransitionType {
  object Horizontal : RouterTransitionType()
  object Vertical : RouterTransitionType()
  object Fade : RouterTransitionType()
  object None : RouterTransitionType()
  data class Shared(val transition: SharedElementTransition) : RouterTransitionType()
}
