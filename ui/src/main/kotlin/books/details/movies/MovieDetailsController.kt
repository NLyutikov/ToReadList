package ru.appkode.base.ui.books.details.movies

import android.view.View
import androidx.core.view.isVisible
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.movie_details_controller.*
import kotlinx.android.synthetic.main.network_error.*
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
                args.putLong(ARG_MOVIE_ID, movieId)
            }
        }
    }

    private val movieId: Long by lazy { args.getLong(ARG_MOVIE_ID) }

    override fun createConfig(): Config {
        return object : Config {
            override val viewLayoutResource = R.layout.movie_details_controller
        }
    }

    override fun initializeView(rootView: View) {

    }

    override fun renderViewState(viewState: MovieDetailsScreen.ViewState) {
        fieldChanged(viewState, {viewState -> viewState.movieDetailsState }) {
            movie_details_loading.isVisible = viewState.movieDetailsState.isLoading
            movie_details_scrollview.isVisible = viewState.movieDetailsState.isContent
            movie_details_app_bar.isVisible = viewState.movieDetailsState.isContent
            network_error_screen_container.isVisible = viewState.movieDetailsState.isError

            if(viewState.movieDetailsState.isContent)
                showContent(viewState)
        }

        fieldChanged(viewState, {viewState ->  viewState.details?.isInHistory ?: 1}) {
            if (viewState.details != null)
                showHistoryAndWishListButtons(viewState.details.isInHistory, viewState.details.isInWishList)
        }

        fieldChanged(viewState, {viewState ->  viewState.details?.isInWishList ?: 1}) {
            if (viewState.details != null)
                showHistoryAndWishListButtons(viewState.details.isInHistory, viewState.details.isInWishList)
        }
    }

    fun showContent(viewState: MovieDetailsScreen.ViewState) {
        //TODO
    }

    private fun showHistoryAndWishListButtons(isInHistory: Boolean, isInWishList: Boolean) {
        movie_details_add_to_wish_list.isVisible = isInWishList || !isInHistory && !isInWishList
        movie_details_add_to_history.isVisible = isInHistory || !isInHistory && !isInWishList

        when {
            !isInHistory && !isInWishList -> {
                movie_details_add_to_wish_list.setImageResource(R.drawable.outline_turned_in_not_24)
                movie_details_add_to_history.setImageResource(R.drawable.ic_history_24dp)
            }
            isInHistory -> {
                movie_details_add_to_history.setImageResource(R.drawable.ic_history_blue_24dp)
            }
            isInWishList -> {
                movie_details_add_to_wish_list.setImageResource(R.drawable.outline_turned_in_24)
            }
        }
    }

    override fun wishListBtnPressedIntent(): Observable<Unit> {
        return movie_details_add_to_wish_list.clicks()
    }

    override fun historyBtnPressedIntent(): Observable<Unit> {
        return movie_details_add_to_history.clicks()
    }

    override fun createPresenter(): MovieDetailsPresenter {
        return MovieDetailsPresenter(
            DefaultAppSchedulers,
            RepositoryHelper.getBooksLocalRepository(applicationContext!!, DefaultAppSchedulers),
            RepositoryHelper.getBooksNetworkRepository(DefaultAppSchedulers),
            router,
            movieId
        )
    }
}

const val ARG_MOVIE_ID = "movie_id"