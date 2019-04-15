package ru.appkode.base.ui.core.core.util

import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router

fun Router.displayControllerFromBackstackByTag(tag: String, changeHandler: ControllerChangeHandler? = null) {
    val requestedTransaction = backstack.find { it.tag() == tag }
    val newBackstack = ArrayList(backstack.filter { it.tag() != tag })
    newBackstack.add(requestedTransaction)
    setBackstack(newBackstack, changeHandler)
}

fun Router.isControllersWithTagsInBackstack(vararg tag: String, allInBackstack: Boolean = true): Boolean {
    val backstackOnlyWithTagControllersSize = backstack.filter { trans ->
        if (trans.tag() != null) tag.contains(trans.tag()) else false
    }.size
    return  (allInBackstack && backstackOnlyWithTagControllersSize == tag.size) ||
            (!allInBackstack && backstackOnlyWithTagControllersSize >= 0 && backstackOnlyWithTagControllersSize <= tag.size)
}

fun Router.isOnlyControllersWithTagsInBackstack(vararg tag: String): Boolean {
    val backstackWithoutTagControllersSize = backstack.filter { trans ->
        if (trans.tag() != null)
            !tag.contains(trans.tag())
        else
            true
    }.size
    return backstackWithoutTagControllersSize == 0
}

fun Router.deleteAllControllersFromBackstackExcept(
    vararg tag: String,
    changeHandler: ControllerChangeHandler? = null
) {
    val newBackstack = backstack.filter { trans ->
        if (trans.tag() != null) tag.contains(trans.tag()) else false
    }
    setBackstack(newBackstack, changeHandler)
}