package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.data.trendingCuisine
import com.example.recipepool.databinding.LayoutTrendingRecipeBinding

class RecyclerAdapterTrending(private var data: ArrayList<trendingCuisine>) :
    RecyclerView.Adapter<RecyclerAdapterTrending.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutTrendingRecipeBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {

                Glide.with(itemView.context)
                    .load(this.image)
                    .into(binding.img)

            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutTrendingRecipeBinding) : RecyclerView.ViewHolder(binding.root)
}