package com.example.recipepool.recycleradapter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.data.Recipe
import com.example.recipepool.screens.AddRecipe
import com.example.recipepool.screens.RecipePageActivity

class RecyclerAdapterProfileNew (val data: ArrayList<Recipe>?, private val query : String) :
    RecyclerView.Adapter<RecyclerAdapterProfileNew.ViewHolder>()  {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterProfileNew
    .ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.card_search_recipe,parent,false)
        Log.d("inside","search adapter")
        return RecyclerAdapterProfileNew.ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerAdapterProfileNew
        .ViewHolder, position: Int
    ) {

       if (data.isNullOrEmpty()) {

           Glide.with(holder.itemView.context)
               .load(R.drawable.ic_baseline_add_24_new_profile)
               .into(holder.image)

           holder.name.text = "Create a Recipe"

       }
        else{

           if (query == "cookBook" && position == data.size) {

               Glide.with(holder.itemView.context)
                   .load(R.drawable.ic_baseline_add_24_new_profile)
                   .into(holder.image)

               holder.name.text = "Create a Recipe"

           }
           else{
               val input = data[position]
               holder.bind(input)
           }

       }
        holder.itemView.setOnClickListener {

            if (data.isNullOrEmpty()){
                val intent = Intent(holder.itemView.context , AddRecipe::class.java)
                holder.itemView.context.startActivity(intent)
            }
            else{
                val input = data[position]

                if (query == "cookBook" && position == data.size){

                    val intent = Intent(holder.itemView.context , AddRecipe::class.java)
                    holder.itemView.context.startActivity(intent)
                }
                else{
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
        }

    }

    override fun getItemCount(): Int {
         if (query == "cookBook" && data.isNullOrEmpty()) return  1
        else if (query == "cookBook"){
            return data!!.size +1
        }
        else return data!!.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val name: TextView = v.findViewById(R.id.search_rv_text)
        val image: ImageView = v.findViewById(R.id.search_rv_image)

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
