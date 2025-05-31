package com.mcs.dojomovie.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mcs.dojomovie.R
import com.mcs.dojomovie.model.Film

class FilmAdapter(
    private val filmList : ArrayList<Film>,
    private val listener: OnItemClickListener
): RecyclerView.Adapter<FilmAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(film: Film)
    }
    class ViewHolder (filmView: View, private val listener: OnItemClickListener, private val filmList: ArrayList<Film>) : RecyclerView.ViewHolder(filmView) {
        val image: ImageView = filmView.findViewById(R.id.iv_poster_film)
        val judul: TextView = filmView.findViewById(R.id.tv_judul_film)
        val harga: TextView = filmView.findViewById(R.id.tv_harga_film)

        init {
            filmView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(filmList[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): ViewHolder {
        val filmView = LayoutInflater.from(parent.context).inflate(R.layout.item_film, parent, false)
        return ViewHolder(filmView, listener, filmList)
    }

    override fun onBindViewHolder(holder: FilmAdapter.ViewHolder, position: Int) {
        val film = filmList[position]

        val context = holder.itemView.context
        val imageName = film.image

        if (imageName.isNullOrEmpty()) {
            holder.image.setImageDrawable(null)
        } else {
            val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

            if (resId != 0) {
                holder.image.setImageResource(resId)
            }
        }

        holder.judul.text = film.title
        holder.harga.text = "Rp ${film.price}"
    }

    override fun getItemCount(): Int {
        return filmList.size
    }

    fun updateData(newFilms: List<Film>) {
        this.filmList.clear()
        this.filmList.addAll(newFilms)
        notifyDataSetChanged()
    }
}