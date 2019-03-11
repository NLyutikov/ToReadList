package ru.appkode.base.ui.core.core.routing.transition

import android.transition.Transition
import android.view.View
import android.view.ViewGroup

/*
 * Sample
 *
 * ```kotlin
 * class MySharedElementTransition : SharedElementTransition, SharedElementTransitionChangeHandler() {
 *
 *   override fun getExitTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition? {
 *     return Fade(Fade.OUT)
 *   }
 *
 *   override fun getSharedElementTransition(
 *    container: ViewGroup,
 *    from: View?,
 *    to: View?,
 *    isPush: Boolean): Transition? {
 *     return Fade(Fade.OUT)
 *   }
 *
 *   override fun getEnterTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition? {
 *     return Fade(Fade.IN)
 *   }
 *
 *   override fun configureSharedElements(
 *     container: ViewGroup, from: View?, to: View?, isPush: Boolean
 *   ) {
 *     addSharedElement("shared_element_name")
 *   }
 * }
 * ```
 */
interface SharedElementTransition {
  fun getSharedElementTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition?

  fun getExitTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition?

  fun getEnterTransition(container: ViewGroup, from: View?, to: View?, isPush: Boolean): Transition?

  fun configureSharedElements(
    container: ViewGroup,
    from: View?,
    to: View?,
    isPush: Boolean
  )
}
