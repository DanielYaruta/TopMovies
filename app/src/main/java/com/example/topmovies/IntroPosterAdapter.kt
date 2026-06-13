package com.example.topmovies

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.topmovies.databinding.ItemIntroPosterBinding

class IntroPosterAdapter(private val urls: List<String>) :
    RecyclerView.Adapter<IntroPosterAdapter.VH>() {

    class VH(val binding: ItemIntroPosterBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemIntroPosterBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount() = urls.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        Glide.with(holder.binding.root)
            .load(urls[position])
            .centerCrop()
            .into(holder.binding.ivPoster)
    }
}
