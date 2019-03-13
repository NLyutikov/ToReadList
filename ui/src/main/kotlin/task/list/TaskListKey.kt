package ru.appkode.base.ui.task.list

import com.bluelinelabs.conductor.Controller
import kotlinx.android.parcel.Parcelize
import ru.appkode.base.ui.core.core.model.ScreenKey

@Parcelize
internal class TaskListKey: ScreenKey() {
    override fun createController(): Controller {
        return TaskListController()
    }
}