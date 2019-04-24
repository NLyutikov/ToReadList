package ru.appkode.base.ui.books.details.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jakewharton.rxrelay2.PublishRelay
import kotlinx.android.synthetic.main.trailers_list_item.view.*
import movies.details.VideoResult
import ru.appkode.base.entities.core.movies.YOUTUBE_BASE_URL
import ru.appkode.base.ui.R
import ru.appkode.base.ui.core.core.util.filterEvents

class TrailersMovieAdapter : RecyclerView.Adapter<TrailersMovieAdapter.ViewHolder>() {

    var data: List<VideoResult> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private val eventsRelay = PublishRelay.create<Pair<Int, String?>>()

    val trailerClicked = eventsRelay.filterEvents(TRAILER_MOVIE_EVENT_ID_ITEM_CLICKED)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrailersMovieAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.trailers_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: TrailersMovieAdapter.ViewHolder, position: Int) {
        with(holder) {
            title.text = data[position].name
            Glide.with(view.context)
                .load(getYoutubeThumbnailUrl(data[position].key))
                .into(image)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.trailer_name_text
        val image: ImageView = view.trailer_thumbnail

        init {
            view.setOnClickListener {
                eventsRelay.accept(TRAILER_MOVIE_EVENT_ID_ITEM_CLICKED to data[adapterPosition].key)
            }
        }
    }

    private fun getYoutubeThumbnailUrl(key: String?) = "$YOUTUBE_BASE_URL$key/0.jpg"
}

const val TRAILER_MOVIE_EVENT_ID_ITEM_CLICKED = 71
