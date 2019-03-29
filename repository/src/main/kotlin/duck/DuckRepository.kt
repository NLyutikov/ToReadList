package ru.appkode.base.repository.duck

import io.reactivex.Single
import ru.appkode.base.entities.core.duck.DuckUM

interface DuckRepository {
  fun ducks(): Single<List<DuckUM>>
}
