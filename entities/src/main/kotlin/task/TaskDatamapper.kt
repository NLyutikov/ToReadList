package ru.appkode.base.entities.core.task

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
