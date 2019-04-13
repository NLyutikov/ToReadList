package ru.appkode.base.data.network

import com.squareup.moshi.Moshi
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.data.network.duck.DuckApi
import ru.appkode.ui.core.BuildConfig

object NetworkHelper {
  const val DUCK_API_BASE_URL = "https://duck-appkode.herokuapp.com"
  const val BOOKS_BASE_URL = "https://www.goodreads.com/"
  const val DUCK_API_IMAGE_URL = "$DUCK_API_BASE_URL/static"
  const val API_KEY = "lEJVSEkHbRKXduThStEg9w"

  private val moshi = Moshi.Builder()
    .build()

  private val xml = TikXml.Builder()
    .exceptionOnUnreadXml(false)
    .build()

  private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) BODY else NONE))
    .build()

  private val okHttpClientWithApiKey = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) BODY else NONE))
    .addInterceptor(object : Interceptor {//FIXME написать как отдельный кдасс
      override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val url = original.url()
          .newBuilder()
          .addQueryParameter("key", API_KEY)
          .build()

        val request = original.newBuilder()
          .url(url)
          .build()

        return chain.proceed(request)
      }
    })
    .build()

  private val duckApi = Retrofit.Builder()
    .baseUrl(DUCK_API_BASE_URL)
    .client(okHttpClient)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(DuckApi::class.java)

  private val booksApi = Retrofit.Builder()
    .baseUrl(BOOKS_BASE_URL)
    .client(okHttpClientWithApiKey)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(TikXmlConverterFactory.create(xml))
    .build()
    .create(BooksApi::class.java)

  fun getBooksApi() = booksApi

  fun getDuckApi(): DuckApi = duckApi
}
