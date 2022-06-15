package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
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

                if(this.image != null) {
                    Glide.with(itemView.context)
                        .load(this.image)
                        .into(binding.img)
                }
                else {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_launcher_background).
                        into(binding.img)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutTrendingRecipeBinding) : RecyclerView.ViewHolder(binding.root)
}