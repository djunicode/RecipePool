package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.IngredientList
import com.example.recipepool.databinding.LayoutIngredientsBinding

class RecyclerAdapterIngredients(private var data: List<IngredientList>) :
    RecyclerView.Adapter<RecyclerAdapterIngredients.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutIngredientsBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.textIngredientName.text = this.name
                binding.textIngredientAmount.text = this.amount
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutIngredientsBinding) : RecyclerView.ViewHolder(binding.root)
}