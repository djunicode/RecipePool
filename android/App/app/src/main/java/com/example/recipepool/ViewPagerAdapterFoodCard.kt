package com.example.recipepool

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.databinding.LayerListCardBinding

class ViewPagerAdapterFoodCard(private val context: Context, private val data: List<FoodList>) :
    RecyclerView.Adapter<ViewPagerAdapterFoodCard.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerAdapterFoodCard.ViewHolder {
        val binding = LayerListCardBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewPagerAdapterFoodCard.ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.time.text = this.time
                binding.foodNameText.text = this.name
                binding.ratingBar.rating = this.rating
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayerListCardBinding) : RecyclerView.ViewHolder(binding.root)
}