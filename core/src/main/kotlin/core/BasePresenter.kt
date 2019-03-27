package ru.appkode.base.ui.core.core

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import ru.appkode.base.ui.core.core.util.AppSchedulers
import ru.appkode.base.ui.core.core.util.skipFirstIf
import ru.appkode.base.ui.core.core.util.toLceEventObservable
import ru.appkode.ui.core.BuildConfig
import timber.log.Timber

@Suppress("TooManyFunctions")
abstract class BasePresenter<V : MviView<VS>, VS, A : Any>(
  protected val schedulers: AppSchedulers,
  private val skipRenderOfInitialState: Boolean = false,
  private val logStateChanges: Boolean = !BuildConfig.RELEASE
) : MviBasePresenter<V, VS>() {

  /**
   * Emits output actions from commands produced by reducer.
   * These actions will be delivered to the start of MVI-loop and be processed
   * by reducers.
   */
  private val outputActions = PublishRelay.create<A>()
  private val disposables = CompositeDisposable()

  final override fun bindIntents() {
    val stateChanges = Observable.merge(createIntents().plus(outputActions))
      .scan(Pair<VS, Command<A>?>(createInitialState(), null)) { s, a ->
        val (ps, _) = s
        val (ns, cmd) = reduceViewState(ps, a)

        if (logStateChanges) logStateChanges(ns, a)

        Pair(ns, cmd)
      }
      .skipFirstIf(skipRenderOfInitialState)
      .observeOn(schedulers.ui)
      .doAfterNext { (_, cmd) ->
        val nextAction = cmd?.invoke()
        if (nextAction != null) outputActions.accept(nextAction)
      }
      .map { (vs, _) -> vs }
      .distinctUntilChanged()

    subscribeViewState(stateChanges) { view, viewState -> view.render(viewState) }
  }

  override fun unbindIntents() {
    disposables.dispose()
    super.unbindIntents()
  }

  protected abstract fun reduceViewState(previousState: VS, action: A): Pair<VS, Command<A>?>

  protected abstract fun createIntents(): List<Observable<out A>>

  protected abstract fun createInitialState(): VS

  private fun logStateChanges(ns: VS, a: A) {
    Timber.tag(this.javaClass.simpleName)
    Timber.d(
      """
            ┌─────────────────────────────────────────────────────────
            │ Reduce after action:
            │
            │ $a
            │
            │ New state is:
            │
            │ $ns
            └──────────────────────────────────────────────────────────
            """.trimIndent()
    )
  }

  protected fun <T> Observable<T>.doLceAction(
    actionCreator: (LceState<T>) -> A
  ): Observable<A> {
    return this
      .observeOn(schedulers.ui)
      .toLceEventObservable(actionCreator)
      .doAfterNext { outputActions.accept(it) }
  }

  protected fun <T> Observable<T>.doAction(
    actionCreator: (T) -> A
  ): Observable<A>{
    return this.map {
      val action = actionCreator(it)
      outputActions.accept(action)
      action
    }
  }

  protected fun <T> Observable<T>.safeSubscribe() {
    disposables.add(this.subscribe())
  }

  protected fun Completable.doLceAction(
    actionCreator: (LceState<Unit>) -> A
  ): Observable<A> {
    return this
      .toLceEventObservable(actionCreator)
      .doAfterNext { outputActions.accept(it) }
      .observeOn(schedulers.ui)
  }

  protected fun Completable.doAction(
    actionCreator: () -> A
  ): Completable{
    return this.doFinally { outputActions.accept(actionCreator()) }
  }

  protected fun Completable.safeSubscribe() {
    disposables.add(this.subscribe())
  }

  /**
   * Функция для создания события при первом открытии экрана
   * Когда не важна последоватьльность выполнения intent'ов
   * Её следует использовать в функции ```createIntents()```
   */
  fun <T> bootstrapper(action: () -> Unit): Observable<T> {
    return Completable
      .fromAction(action)
      .toObservable()
  }

  /**
   * Функция для создания события при первом открытии экрана
   * Когда важно чтобы `action` выполнился после подписки на Observable
   * Её следует использовать в функции ```createIntents()```
   */
  fun <T> Observable<T>.withBootstrapper(action: () -> Unit): Observable<T> {
    return mergeWith(bootstrapper(action))
  }
}
