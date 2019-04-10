package ru.appkode.base.entities.core.books

import kotlin.math.max

object Format {
    //Есть ли смысл в данном случае писать inline?
    inline fun <T>toLine(collection: Collection<T>, range: IntRange, filter:(T) -> String): String {
        return collection.drop(range.start)
            .take(range.endInclusive - range.first + 1)
            .map { item -> filter(item) }
            .reduce { line, item -> "$line, $item" }
    }
}