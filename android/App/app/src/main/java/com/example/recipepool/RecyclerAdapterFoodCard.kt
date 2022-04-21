package com.example.recipepool

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.databinding.LayerListCardBinding

class RecyclerAdapterFoodCard(private var data: List<FoodList>) :
    RecyclerView.Adapter<RecyclerAdapterFoodCard.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayerListCardBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.time.text = this.time
                binding.foodNameText.text = this.name
                binding.ratingBar.rating = this.rating

                when (position % 3) {
                    0 -> binding.foodCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_color_1))
                    1 -> binding.foodCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_color_2))
                    2 -> binding.foodCard.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.card_color_3))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayerListCardBinding) : RecyclerView.ViewHolder(binding.root)
}