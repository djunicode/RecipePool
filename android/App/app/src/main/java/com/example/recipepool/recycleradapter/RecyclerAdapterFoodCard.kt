package com.example.recipepool.recycleradapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.data.Recipe
import com.example.recipepool.databinding.CardLayerListBinding
import com.example.recipepool.screens.RecipePageActivity

class RecyclerAdapterFoodCard(private var data: List<Recipe>) :
    RecyclerView.Adapter<RecyclerAdapterFoodCard.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardLayerListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.time.text = this.totalTime
                binding.foodNameText.text = this.recipeName

                if(this.image != null) {
                    Glide.with(itemView.context)
                        .load("https://therecipepool.pythonanywhere.com" + this.image)
                        .into(binding.foodImage)
                }
                else {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_launcher_background).
                        into(binding.foodImage)
                }

                when (position % 3) {
                    0 -> binding.foodCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.card_color_1
                        )
                    )
                    1 -> binding.foodCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.card_color_2
                        )
                    )
                    2 -> binding.foodCard.setCardBackgroundColor(
                        ContextCompat.getColor(
                            itemView.context,
                            R.color.card_color_3
                        )
                    )
                }

                binding.foodCard.setOnClickListener {
                    val intent = Intent(itemView.context, RecipePageActivity::class.java)
                    val bundle = Bundle()
                    bundle.putParcelableArrayList("ingredient_list", ingredientList)
                    bundle.putParcelableArrayList("step_list", stepsList)
                    intent.putExtra("recipe_id", this.id)
                    intent.putExtra("recipe_name", recipeName)
                    intent.putExtra("likes", likes)
                    intent.putExtra("liked", liked)
                    intent.putExtra("time", totalTime)
                    intent.putExtra("images", image)
                    intent.putExtras(bundle)
                    itemView.context.startActivity(intent)
                }

                binding.favourite.setOnClickListener {

                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: CardLayerListBinding) :
        RecyclerView.ViewHolder(binding.root)
}