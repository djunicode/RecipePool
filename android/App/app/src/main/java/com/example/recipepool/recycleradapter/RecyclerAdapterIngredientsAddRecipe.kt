package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.data.Ingredients

class RecyclerAdapterIngredientsAddRecipe(val data: ArrayList<Ingredients>) :
    RecyclerView.Adapter<RecyclerAdapterIngredientsAddRecipe.ViewHolder>() {

    fun deleteItem(i:Int){
        data.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_add_ingredients,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val input = data[position]
        holder.bind(input)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val igName = v.findViewById<TextView>(R.id.textIgName)
        val qt = v.findViewById<TextView>(R.id.textQt)
        fun bind(data:Ingredients){
            igName.text = data.ingredient.toString()
            qt.text = data.quantity.toString()
        }
    }

}