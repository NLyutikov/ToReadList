package ru.appkode.base.ui.core.core.util

import android.os.SystemClock
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers

/**
 * Swaps out an original source with a new one which will complete after a small delay.
 * Can be used to aid quick testing without removing an original source from chain.
 *
 * ```
 * myNetworkService.doSomeRequest() // will never get executed, will be substituted with passed mock result
 *   .hotSwapWithSuccess(3)
 *   .subscribe(...)
 * ```
 */
fun <T> Observable<T>.hotSwapWithSuccess(
    result: T,
    delayMillis: Long = MOCK_NOTIFICATION_DEFAULT_DELAY_MS
): Observable<T> {
    return Observable
        .fromCallable {
            SystemClock.sleep(delayMillis)
            result
        }
        .subscribeOn(Schedulers.io())
}

/**
 * Swaps out an original source with a new one which will return an error after a small delay.
 * Can be used to aid quick testing without removing an original source from chain.
 *
 * ```
 * myNetworkService.doSomeRequest() // will never get executed, error will be triggered instead
 *   .hotSwapWithError(fakeError)
 *   .subscribe(...)
 * ```
 */
fun <T> Observable<T>.hotSwapWithError(
    error: Throwable,
    delayMillis: Long = MOCK_NOTIFICATION_DEFAULT_DELAY_MS
): Observable<T> {
    return Observable
        .fromCallable<T> {
            SystemClock.sleep(delayMillis)
            throw error
        }
        .subscribeOn(Schedulers.io())
}

fun <T> Observable<T>.skipFirstIf(value: Boolean): Observable<T> {
    return if (value) this.skip(1) else this
}

/**
 * @see
 * http://rxmarbles.com/#pairwise
 */
fun <T> Observable<T>.pairwise(): Observable<Pair<T, T>> {
    return publish {
        Observable.zip(
            it,
            it.skip(1),
            BiFunction { v1: T, v2: T -> v1 to v2 }
        )
    }
}

private const val MOCK_NOTIFICATION_DEFAULT_DELAY_MS = 1500L
