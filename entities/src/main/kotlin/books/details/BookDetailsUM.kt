package ru.appkode.base.entities.core.books.details

import org.intellij.lang.annotations.Language

data class BookDetailsUM(
    val id: Int,
    val title: String? = null,
    val isbn: String? = null,
    val isbn13: String? = null,
    val language: String? = null,
    val coverImageUrl: String? = null,
    val smallCoverImageUrl: String? = null,
    val pagesNumber: Int? = null,
    val description: String? = null,
    val ratingsCount: Int? = null,
    val authors: List<AuthorUM>? = null,
    val similarBooks: List<BookDetailsUM>? = null
)

data class AuthorUM(
    val id: Int,
    val name: String? = null
)