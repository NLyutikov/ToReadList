package ru.appkode.base.ui.duck

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.duck_list_item.view.*
import ru.appkode.base.entities.core.duck.DuckUM
import ru.appkode.base.ui.R

class DuckListAdapter : RecyclerView.Adapter<DuckListAdapter.ViewHolder>() {

  var data: List<DuckUM> = emptyList()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.duck_list_item, parent, false))
  }

  override fun getItemCount() = data.size

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    with(holder) {
      name.text = data[position].name
      Picasso.get().load(data[position].imageUrl).into(image)
    }
  }

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val name: TextView = view.duck_list_item_name
    val image: ImageView = view.duck_list_item_image
  }
}
