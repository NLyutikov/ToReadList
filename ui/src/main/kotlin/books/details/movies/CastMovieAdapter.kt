package ru.appkode.base.ui.books.details.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.movie_cast_details_list_item.view.*
import movies.details.Cast
import ru.appkode.base.entities.core.movies.BASE_MOVIE_IMAGE_URL
import ru.appkode.base.entities.core.movies.PROFILE_SIZE
import ru.appkode.base.ui.R

class CastMovieAdapter : RecyclerView.Adapter<CastMovieAdapter.ViewHolder>() {

    var data: List<Cast> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastMovieAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_cast_details_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: CastMovieAdapter.ViewHolder, position: Int) {
        with(holder) {
            title.text = data[position].name
            Glide.with(view.context)
                .load(BASE_MOVIE_IMAGE_URL + PROFILE_SIZE + data[position].profile_path)
                .centerCrop()
                .into(image)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.cast_name_text
        val image: ImageView = view.details_cast_image
    }
}