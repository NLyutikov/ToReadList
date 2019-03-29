package ru.appkode.base.entities.core.duck

import ru.appkode.base.entities.core.util.requireField

fun DuckNM.toUiModel(baseImageUrl: String): DuckUM {
  return DuckUM(
    name = name.requireField("name"),
    imageUrl = "$baseImageUrl/${image.requireField("image")}"
  )
}

fun List<DuckNM>.toUiModel(baseImageUrl: String): List<DuckUM> {
  return map { it.toUiModel(baseImageUrl) }
}
