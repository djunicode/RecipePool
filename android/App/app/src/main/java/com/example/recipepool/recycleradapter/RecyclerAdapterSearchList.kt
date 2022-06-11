package com.example.recipepool.recycleradapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.data.Recipe
import com.example.recipepool.screens.RecipePageActivity


class RecyclerAdapterSearchList(val data: ArrayList<Recipe>) :
    RecyclerView.Adapter<RecyclerAdapterSearchList.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

       /* val binding = CardSearchRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return ViewHolder(binding)*/

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_search_recipe,parent,false)
        Log.d("inside","search adapter")
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val input = data[position]
        holder.bind(input)

        holder.itemView.setOnClickListener {

            val intent = Intent(holder.itemView.context, RecipePageActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelableArrayList("ingredient_list", input.ingredientList)
            bundle.putParcelableArrayList("step_list", input.stepsList)
            intent.putExtra("recipe_id", input.id)
            intent.putExtra("recipe_name", input.recipeName)
            intent.putExtra("likes", input.likes)
            intent.putExtra("time", input.totalTime)
            intent.putExtra("images", input.image)
            intent.putExtras(bundle)
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.search_rv_text)
        val image: ImageView = v.findViewById(R.id.search_rv_image)
        val card: CardView = v.findViewById(R.id.searchCard2)


        fun bind(data: Recipe){
            name.text = data.recipeName.toString()

            if(data.image != null) {
                Glide.with(itemView.context)
                    .load("https://therecipepool.pythonanywhere.com" + data.image.toString())
                    // .apply(RequestOptions().override(150,150))
                    .into(image)
            }
            else {
                Glide.with(itemView.context)
                    .load(R.drawable.ic_launcher_background).
                    into(image)
            }
        }
    }
}