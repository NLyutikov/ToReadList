package ru.appkode.base.repository

import android.content.Context
import ru.appkode.base.data.network.NetworkHelper
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.repository.books.BooksLocalRepository
import ru.appkode.base.repository.books.BooksLocalRepositoryImpl
import ru.appkode.base.repository.books.BooksNetworkRepository
import ru.appkode.base.repository.books.BooksNetworkRepositoryImpl
import ru.appkode.base.ui.core.core.util.AppSchedulers

object RepositoryHelper {

  fun getBooksNetworkRepository(schedulers: AppSchedulers): BooksNetworkRepository {
    return BooksNetworkRepositoryImpl(schedulers, NetworkHelper.getBooksApi())
  }

  fun getBooksLocalRepository(context: Context, schedulers: AppSchedulers): BooksLocalRepository {
    return BooksLocalRepositoryImpl(
      schedulers,
      DatabaseHelper.getWishListPersistence(),
      DatabaseHelper.getHistoryPersistence(),
      context
    )
  }

}
