package ru.appkode.base.repository.duck

import io.reactivex.Observable
import ru.appkode.base.entities.core.duck.DuckUM

interface DuckRepository {
  fun ducks(): Observable<List<DuckUM>>
}
