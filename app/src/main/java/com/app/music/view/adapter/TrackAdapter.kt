package com.app.music.view.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.app.music.R
import com.app.music.domain.Track
import com.app.music.service.restservices.SearchManager
import de.hdodenhof.circleimageview.CircleImageView
import soup.neumorphism.NeumorphImageView

interface OnTrackClickInterface{
    fun onClick(item: Track)
}
class TrackAdapter(private val tracks:List<Track>, private val clickListener: OnTrackClickInterface) : RecyclerView.Adapter<TrackViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.track_item_layout, parent, false)
        return TrackViewHolder(view)
    }



    override fun getItemCount(): Int {
        return tracks.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.title?.text = tracks[position].title
        holder.artist?.text = tracks[position].artistName
        holder.play_button?.setOnClickListener{
            clickListener.onClick(tracks[position])
        }

        Glide.with(holder.coverImage.context).load(SearchManager.getUrlWithHeaders(tracks[position].albumId))
            .into(holder.coverImage)
    }
}
class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var play_button: NeumorphImageView?=null
    var title:TextView?=null
    var artist:TextView?=null
    var coverImage:CircleImageView
    init{
        title=itemView.findViewById(R.id.text_view_title)
        artist=itemView.findViewById(R.id.text_view_artist)
        play_button=itemView.findViewById(R.id.play_button)
        coverImage=itemView.findViewById(R.id.track_image)
    }
}