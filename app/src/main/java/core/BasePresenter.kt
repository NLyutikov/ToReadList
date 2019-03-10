package ru.appkode.base.core

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.BuildConfig
import ru.appkode.base.core.util.AppSchedulers
import ru.appkode.base.core.util.skipFirstIf
import timber.log.Timber

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

private const val DEBUG_ACTION_NAME_MAX_LENGTH = 80
private const val DEBUG_STATE_CONTENTS_MAX_LENGTH = 80 * 4
