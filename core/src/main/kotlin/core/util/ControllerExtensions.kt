package ru.appkode.base.ui.core.core.util

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler
import ru.appkode.base.ui.core.core.model.SCREEN_KEY_ARG_NAME
import ru.appkode.base.ui.core.core.model.ScreenKey

inline val Controller.requireResources get() = this.resources!!
inline val Controller.requireActivity get() = this.activity!!
inline val Controller.requireView get() = this.view!!

fun <T : ScreenKey> Controller.key(): T {
  return args.getParcelable(SCREEN_KEY_ARG_NAME)
    ?: throw IllegalStateException("controller was not constructed with key")
}

private const val SLIDE_BOTTOM_PUSH_ANIM_DURATION = 300L
private const val SLIDE_BOTTOM_POP_ANIM_DURATION = 200L
private const val SLIDE_HORIZONTAL_PUSH_ANIM_DURATION = 300L
private const val SLIDE_HORIZONTAL_POP_ANIM_DURATION = 200L
private const val SLIDE_FADE_PUSH_ANIM_DURATION = 300L
private const val SLIDE_FADE_POP_ANIM_DURATION = 200L

fun createDefaultHorizontalChangeHandler(isPush: Boolean): HorizontalChangeHandler {
  val duration = if (isPush) SLIDE_HORIZONTAL_PUSH_ANIM_DURATION else SLIDE_HORIZONTAL_POP_ANIM_DURATION
  return HorizontalChangeHandler(duration)
}

fun createDefaultVerticalChangeHandler(isPush: Boolean): VerticalChangeHandler {
  val duration = if (isPush) SLIDE_BOTTOM_PUSH_ANIM_DURATION else SLIDE_BOTTOM_POP_ANIM_DURATION
  return VerticalChangeHandler(duration)
}

fun Controller.obtainVerticalTransaction(): RouterTransaction {
  return RouterTransaction
    .with(this)
    .pushChangeHandler(createDefaultVerticalChangeHandler(true))
    .popChangeHandler(createDefaultVerticalChangeHandler(false))
}

fun Controller.obtainHorizontalTransaction(): RouterTransaction {
  return RouterTransaction
    .with(this)
    .pushChangeHandler(createDefaultHorizontalChangeHandler(true))
    .popChangeHandler(createDefaultHorizontalChangeHandler(false))
}

fun Controller.obtainFadeTransaction(): RouterTransaction {
  return RouterTransaction
    .with(this)
    .pushChangeHandler(FadeChangeHandler(SLIDE_FADE_PUSH_ANIM_DURATION))
    .popChangeHandler(FadeChangeHandler(SLIDE_FADE_POP_ANIM_DURATION))
}
