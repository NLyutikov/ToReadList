package ru.appkode.base.ui.core.core.util

import io.reactivex.Completable
import io.reactivex.Observable
import ru.appkode.base.ui.core.core.LceState

fun <Event> Completable.toLceEventObservable(stateCreator: (LceState<Unit>) -> Event): Observable<Event> {
  return this.andThen(Observable.fromCallable { stateCreator(LceState.Content(Unit)) })
    .onErrorReturn { stateCreator(LceState.Error(it.message!!)) }
    .startWith(stateCreator(LceState.Loading()))
}

fun <Event> Completable.toLceEventObservable(
  onSuccess: () -> Event,
  onError: (Throwable) -> Event,
  onLoading: Event
): Observable<Event> {
  return this
    .andThen(Observable.fromCallable(onSuccess))
    .onErrorReturn(onError)
    .startWith(onLoading)
}