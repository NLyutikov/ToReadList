package ru.appkode.base.repository.duck

import io.reactivex.Single
import ru.appkode.base.data.network.NetworkHelper
import ru.appkode.base.data.network.duck.DuckApi
import ru.appkode.base.entities.core.duck.DuckUM
import ru.appkode.base.entities.core.duck.toUiModel

class DuckRepositoryImpl(private val duckApi: DuckApi) : DuckRepository {
  override fun ducks(): Single<List<DuckUM>> {
    return duckApi.getDuckList()
      .map { it.toUiModel(NetworkHelper.DUCK_API_IMAGE_URL) }
  }
}
