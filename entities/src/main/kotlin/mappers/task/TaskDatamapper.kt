package ru.appkode.base.entities.core.mappers.task

import ru.appkode.base.entities.core.datasource.task.TaskSM
import ru.appkode.base.entities.core.ui.task.TaskUM

fun TaskSM.toUiModel(): TaskUM {
  return TaskUM(
    id,
    title,
    description,
    isChecked
  )
}

fun TaskUM.toStorageModel(): TaskSM {
  return TaskSM(
    id,
    title,
    description,
    isChecked
  )
}