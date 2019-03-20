package ru.appkode.base.entities.core.datasource.task

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task")
data class TaskSM(
  @PrimaryKey(autoGenerate = true)
  val id: Long,
  val title: String,
  val description: String,
  val isChecked: Boolean
)