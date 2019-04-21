package ru.appkode.base.ui.books.details.movies

import android.view.View
import books.details.books.BookDetailsController
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers

class MovieDetailsController() :
    BaseMviController<MovieDetailsScreen.ViewState, MovieDetailsScreen.View, MovieDetailsPresenter>(),
    MovieDetailsScreen.View {

    companion object {
        fun createController(movieId: Long): MovieDetailsController {
            return MovieDetailsController().apply {
                args.putLong(ARG_MOVIE_ID, Math.abs(movieId))
            }
        }
    }

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.movie_details_controller
        }
    }

    override fun initializeView(rootView: View) {

    }

    override fun renderViewState(viewState: MovieDetailsScreen.ViewState) {

    }

    override fun createPresenter(): MovieDetailsPresenter {
        return MovieDetailsPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router
        )
    }
}

const val ARG_MOVIE_ID = "movie_id"