package com.example.topmovies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.topmovies.data.Cast
import com.example.topmovies.databinding.ItemCastBinding

class CastAdapter(private val cast: List<Cast>) :
    RecyclerView.Adapter<CastAdapter.CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CastViewHolder(ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) =
        holder.bind(cast[position])

    override fun getItemCount() = cast.size

    class CastViewHolder(private val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cast: Cast) {
            binding.tvActorName.text = cast.name
            binding.tvCharacter.text = cast.character
            Glide.with(binding.root)
                .load("https://image.tmdb.org/t/p/w185${cast.profilePath}")
                .placeholder(android.R.color.darker_gray)
                .circleCrop()
                .into(binding.ivActor)
        }
    }
}
