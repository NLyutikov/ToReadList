package ru.appkode.base.ui.core.core

/**
 * Обобщает набор состояний Loading/Content/Error.
 */
data class LceState<out Content>(
  val isLoading: Boolean,
  val content: Content?,
  val error: String?
) {

  companion object {
    @Suppress("FunctionName", "FunctionNaming") // constructor function, caps ok
    fun <Content> Loading(content: Content? = null): LceState<Content> {
      return LceState(isLoading = true, content = content, error = null)
    }

    @Suppress("FunctionName", "FunctionNaming") // constructor function, caps ok
    fun <Content> Content(content: Content): LceState<Content> {
      return LceState(isLoading = false, content = content, error = null)
    }

    @Suppress("FunctionName", "FunctionNaming") // constructor function, caps ok
    fun <Content> Error(error: String, content: Content? = null): LceState<Content> {
      return LceState(isLoading = false, content = content, error = error)
    }
  }

  val isContent get() = !isLoading && error == null
  val isError get() = error != null

  fun asContent(): Content {
    check(!isLoading) { "expected content state, but isLoading=true" }
    check(error == null) { "expected content state, but error != null" }
    return content ?: throw IllegalStateException("expected content state, but content is null")
  }

  fun asError(): String {
    check(!isLoading) { "expected content state, but isLoading=true" }
    // not checking content == null, because error can happen after content is set
    return error ?: throw IllegalStateException("expected error state, but error is null")
  }
}