package ru.appkode.base.ui.task.list.entities

data class TaskUM(
    val id: Long,
    val title: String,
    val description: String,
    val isChecked: Boolean
)
