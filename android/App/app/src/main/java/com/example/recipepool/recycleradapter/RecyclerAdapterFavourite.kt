package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.recipepool.R
import com.example.recipepool.data.Recipe
import com.example.recipepool.databinding.CardSearchRecipeBinding

class RecyclerAdapterFavourite(private var data: ArrayList<Recipe>?) :
    RecyclerView.Adapter<RecyclerAdapterFavourite.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerAdapterFavourite.ViewHolder {
        val binding = CardSearchRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerAdapterFavourite.ViewHolder, position: Int) {
        with(holder) {
            with(data?.get(position)) {
                binding.searchRvText.text = this?.recipeName

                if(this?.image != null) {
                    Glide.with(itemView.context)
                        .load("https://therecipepool.pythonanywhere.com" + this?.image.toString())
                        .into(binding.searchRvImage)
                }
                else {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_launcher_background).
                        into(binding.searchRvImage)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data!!.size
    }

    inner class ViewHolder(val binding: CardSearchRecipeBinding) :
        RecyclerView.ViewHolder(binding.root)
}