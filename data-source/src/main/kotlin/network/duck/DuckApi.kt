package ru.appkode.base.data.network.duck

import io.reactivex.Observable
import retrofit2.http.GET

interface DuckApi {
  @GET("ducks")
  fun getDuckList(): Observable<GetDuckListResponse>
}
