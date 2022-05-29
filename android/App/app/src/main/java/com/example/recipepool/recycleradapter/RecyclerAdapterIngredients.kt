package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.data.Ingredients
import com.example.recipepool.databinding.LayoutIngredientsBinding

class RecyclerAdapterIngredients(private var data: List<Ingredients>) :
    RecyclerView.Adapter<RecyclerAdapterIngredients.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutIngredientsBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.textIngredientName.text = this.ingredient
                binding.textIngredientAmount.text = this.quantity
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutIngredientsBinding) : RecyclerView.ViewHolder(binding.root)
}