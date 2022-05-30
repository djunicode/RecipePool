package com.example.recipepool.recycleradapter

import android.content.Intent
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
import com.example.recipepool.data.SearchList
import com.example.recipepool.screens.RecipePageActivity


class RecyclerAdapterSearchList(val data: ArrayList<SearchList>) :
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
            intent.putExtra("recipe_name", input.id)
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.findViewById<TextView>(R.id.search_rv_text)
        val image = v.findViewById<ImageView>(R.id.search_rv_image)
        val card = v.findViewById<CardView>(R.id.searchCard2)


        fun bind(data: SearchList){
            name.text = data.label.toString()

            Glide.with(itemView.context)
                .load("https://therecipepool.pythonanywhere.com" + data.image.toString())
               // .apply(RequestOptions().override(150,150))
                .into(image)
        }
    }
}