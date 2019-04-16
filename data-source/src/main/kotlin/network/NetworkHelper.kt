package ru.appkode.base.data.network

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
import ru.appkode.base.data.network.NetworkHelper.API_KEY
import ru.appkode.base.data.network.books.BooksApi
import ru.appkode.ui.core.BuildConfig

object NetworkHelper {
  const val BOOKS_BASE_URL = "https://www.goodreads.com/"
  const val API_KEY = "lEJVSEkHbRKXduThStEg9w"
  private val xml = TikXml.Builder()
    .exceptionOnUnreadXml(false)
    .build()

  private val okHttpClientWithApiKey = OkHttpClient.Builder()
    .addInterceptor(HttpLoggingInterceptor().setLevel(if (BuildConfig.DEBUG) BODY else NONE))
    .addInterceptor(KeyInterceptor)
    .build()

  private val booksApi = Retrofit.Builder()
    .baseUrl(BOOKS_BASE_URL)
    .client(okHttpClientWithApiKey)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
    .addConverterFactory(TikXmlConverterFactory.create(xml))
    .build()
    .create(BooksApi::class.java)

  fun getBooksApi(): BooksApi = booksApi

}

object KeyInterceptor : Interceptor {
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
}
