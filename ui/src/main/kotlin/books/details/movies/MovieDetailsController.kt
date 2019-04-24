package ru.appkode.base.ui.books.details.movies

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.appbar.AppBarLayout
import com.jakewharton.rxbinding2.view.clicks
import io.reactivex.Observable
import kotlinx.android.synthetic.main.movie_details_controller.*
import kotlinx.android.synthetic.main.network_error.*
import movies.details.Cast
import movies.details.Genre
import movies.details.VideoResult
import ru.appkode.base.entities.core.movies.details.toGenreLine
import ru.appkode.base.repository.RepositoryHelper
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.BaseMviController
import ru.appkode.base.ui.core.core.util.DefaultAppSchedulers
import ru.appkode.base.ui.core.core.util.eventThrottleFirst
import ru.appkode.base.ui.core.core.util.setVisibilityAndText

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

    private lateinit var castMovieAdapter: CastMovieAdapter
    private lateinit var trailersMovieAdapter: TrailersMovieAdapter

    override fun initializeView(rootView: View) {
        castMovieAdapter = CastMovieAdapter()
        trailersMovieAdapter = TrailersMovieAdapter()

        details_cast_list.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        details_trailers_list.layoutManager = LinearLayoutManager(
            applicationContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )

        details_cast_list.adapter = castMovieAdapter
        details_trailers_list.adapter = trailersMovieAdapter
    }

    override fun renderViewState(viewState: MovieDetailsScreen.ViewState) {
        fieldChanged(viewState, { viewState -> viewState.movieDetailsState }) {
            movie_details_loading.isVisible = viewState.movieDetailsState.isLoading
            movie_details_scrollview.isVisible = viewState.movieDetailsState.isContent
            movie_details_app_bar.isVisible = viewState.movieDetailsState.isContent
            network_error_screen_container.isVisible = viewState.movieDetailsState.isError

            if (viewState.movieDetailsState.isContent)
                showContent(viewState)
        }

        fieldChanged(viewState, { viewState -> viewState.details?.isInHistory ?: 1 }) {
            if (viewState.details != null)
                showHistoryAndWishListButtons(viewState.details.isInHistory, viewState.details.isInWishList)
        }

        fieldChanged(viewState, { viewState -> viewState.details?.isInWishList ?: 1 }) {
            if (viewState.details != null)
                showHistoryAndWishListButtons(viewState.details.isInHistory, viewState.details.isInWishList)
        }
    }

    private fun showContent(viewState: MovieDetailsScreen.ViewState) {
        val movie = viewState.details
        check(movie != null)

        movie_details_app_bar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                scrollRange = appBarLayout.totalScrollRange

                if (scrollRange + verticalOffset == 0) {
                    movie_details_toolbar_title.text = movie.title
                    isShow = true
                } else if (isShow) {
                    movie_details_toolbar_title.text = " "
                    isShow = false
                }
            }
        })

        with(movie) {
            showHeader(title, poster, backdrop, releaseDate, runtime, genres)
            showDescription(overview)
            showCast(cast)
            showTrailers(videos)
            showHistoryAndWishListButtons(isInHistory, isInWishList)
        }
    }

    private fun showHeader(
        title: String?,
        poster: String?,
        backdrop: String?,
        releaseDate: String?,
        runtime: Int?,
        genres: List<Genre>?
    ) {
        top.isVisible = !title.isNullOrBlank() || !poster.isNullOrBlank()

        Glide.with(applicationContext!!)
            .load(backdrop)
            .centerCrop()
            .into(details_backdrop)
        Glide.with(applicationContext!!)
            .load(poster)
            .centerCrop()
            .into(details_poster)
        details_subtitle_time.setVisibilityAndText(getDetailsTimeSubtitle(releaseDate, runtime, "hrs", "min"))
        details_subtitle.setVisibilityAndText(title)
        details_subtitle_genres.setVisibilityAndText(genres?.toGenreLine(0..2))
    }

    private fun getDetailsTimeSubtitle(date: String?, runtime: Int?, hrs: String, min: String): String {
        var result = date?.substring(0, 4) + " • "
        val hours = runtime?.div(60)
        val minutes = (runtime?.rem(60))
        if (hours != 0)
            result += " ${hours.toString()} $hrs "
        if (minutes != 0)
            result += " ${minutes.toString()} $min "
        return result
    }

    private fun showDescription(overview: String?) {
        details_overview_subtitle.isVisible = !overview.isNullOrBlank()
        details_description.isVisible = !overview.isNullOrBlank()
        details_description.setVisibilityAndText(overview)
    }

    private fun showCast(cast: List<Cast>?) {
        details_cast_container.isVisible = !cast.isNullOrEmpty()
        castMovieAdapter.data = cast ?: emptyList()
    }

    private fun showTrailers(trailers: List<VideoResult>?) {
        details_trailers_container.isVisible = !trailers.isNullOrEmpty()
        trailersMovieAdapter.data = trailers ?: emptyList()
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

    override fun showMovieTrailerIntent(): Observable<String?> {
        return trailersMovieAdapter.trailerClicked.eventThrottleFirst()
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
