package com.example.recipepool.recycleradapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.data.FoodList
import com.example.recipepool.databinding.CardLayerListBinding
import com.example.recipepool.screens.RecipePageActivity

class RecyclerAdapterFoodCard(private var data: List<FoodList>) :
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
                binding.foodNameText.text = this.label
              //  binding.ratingBar.rating = this.rating!!

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
                    intent.putExtra("recipe_name", this.id)
                    itemView.context.startActivity(intent)
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