package ru.appkode.base.ui.core.core.util

import io.reactivex.Observable

@Suppress("UNCHECKED_CAST")
fun <EvType, EvValue> Observable<Pair<EvType, EvValue>>.filterEvents(type: EvType): Observable<EvValue> {
  return this.filter { it.first == type }.map<EvValue> { it.second }
}

@Suppress("UNCHECKED_CAST")
fun <EvValue> Observable<Pair<Int, Any>>.filterEvents(type: Int): Observable<EvValue> {
  return this.filter { it.first == type }.map<EvValue> { it.second as EvValue }
}
