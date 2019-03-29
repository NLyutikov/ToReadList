package ru.appkode.base.data.network.duck

import io.reactivex.Single
import retrofit2.http.GET
import ru.appkode.base.entities.core.duck.DuckNM

interface DuckApi {
  @GET("ducks")
  fun getDuckList(): Single<List<DuckNM>>
}
