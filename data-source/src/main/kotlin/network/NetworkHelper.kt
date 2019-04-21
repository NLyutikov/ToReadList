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
import ru.appkode.base.data.network.NetworkHelper.API_KEY_BOOKS
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.base.data.network.movies.MovieAPI
import ru.appkode.ui.core.BuildConfig

object NetworkHelper {
  private const val MOVIES_BASE_URL = "https://api.themoviedb.org/"
  private const val BOOKS_BASE_URL = "https://www.goodreads.com/"

  const val API_KEY_BOOKS = "lEJVSEkHbRKXduThStEg9w"
  const val MOVIES_API_KEY = "1774b3433274ef2d5a7baff526aa7f23"

  private val moshi = Moshi.Builder().build()
  private val xml = TikXml.Builder()
    .exceptionOnUnreadXml(false)
    .build()

  private val movieHttpClient = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(if (ru.appkode.base.data.BuildConfig.DEBUG) BODY else NONE))
    .addInterceptor(MovieApiKeyInterceptor)
    .build()

  private val booksOkHttpClientWithApiKey = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) BODY else NONE))
    .addInterceptor(KeyInterceptor)
    .build()

  private val movieApi = Retrofit.Builder()
    .baseUrl(MOVIES_BASE_URL)
    .client(movieHttpClient)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()
    .create(MovieAPI::class.java)

  private val booksApi = Retrofit.Builder()
    .baseUrl(BOOKS_BASE_URL)
    .client(booksOkHttpClientWithApiKey)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(TikXmlConverterFactory.create(xml))
    .build()
    .create(BooksApi::class.java)

  fun getMovieApi(): MovieAPI = movieApi
  fun getBooksApi(): BooksApi = booksApi

}

object MovieApiKeyInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val original = chain.request()
    val url = original.url().newBuilder()
      .addQueryParameter("api_key", NetworkHelper.MOVIES_API_KEY)
      .build()
    return chain.proceed(original.newBuilder().url(url).build())
  }
}

object KeyInterceptor : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    val original = chain.request()
    val url = original.url()
      .newBuilder()
      .addQueryParameter("key", API_KEY_BOOKS)
      .build()

    val request = original.newBuilder()
      .url(url)
      .build()

    return chain.proceed(request)
  }
}
