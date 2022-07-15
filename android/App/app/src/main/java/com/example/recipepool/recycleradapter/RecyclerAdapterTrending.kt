package com.example.recipepool.recycleradapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.data.Recipe
import com.example.recipepool.databinding.LayoutTrendingRecipeBinding
import com.example.recipepool.screens.RecipePageActivity

class RecyclerAdapterTrending(private var data: ArrayList<Recipe>) :
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

            this.itemView.setOnClickListener {
                /*Log.d("data" ,data[position].label.toString() )
                val intent = Intent(holder.itemView.context,Search::class.java)
                intent.putExtra( "cuisine",data[position].label.toString())
                holder.itemView.context.startActivity(intent)*/
                val intent = Intent(itemView.context, RecipePageActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelableArrayList("ingredient_list", data[position].ingredientList)
                bundle.putParcelableArrayList("step_list",  data[position].stepsList)
                intent.putExtra("recipe_id",  data[position].id)
                intent.putExtra("recipe_name",  data[position].recipeName)
                intent.putExtra("likes",  data[position].likes)
                intent.putExtra("time",  data[position].totalTime)
                intent.putExtra("images",  data[position].image)
                intent.putExtras(bundle)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutTrendingRecipeBinding) : RecyclerView.ViewHolder(binding.root)
}