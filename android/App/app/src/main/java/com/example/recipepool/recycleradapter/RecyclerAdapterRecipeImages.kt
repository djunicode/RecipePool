package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.databinding.LayoutRecipeImageBinding

class RecyclerAdapterRecipeImages(private var data: List<Int>) :
    RecyclerView.Adapter<RecyclerAdapterRecipeImages.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutRecipeImageBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                Glide.with(itemView.context)
                    .load(this)
                    .into(binding.recipeImage)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutRecipeImageBinding) : RecyclerView.ViewHolder(binding.root)
}