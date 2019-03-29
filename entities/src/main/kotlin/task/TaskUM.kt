package ru.appkode.base.entities.core.task

data class TaskUM(
    val id: Long,
    val title: String,
    val description: String,
    val isChecked: Boolean
)
