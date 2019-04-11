package ru.appkode.base.entities.core.books

import kotlin.math.max

object Format {
    fun <T>toLine(collection: Collection<T>, range: IntRange, filter:(T) -> String): String {
        return collection.drop(range.start)
            .take(range.endInclusive - range.first + 1)
            .map { item -> filter(item) }
            .reduce { line, item -> "$line, $item" }
    }
}