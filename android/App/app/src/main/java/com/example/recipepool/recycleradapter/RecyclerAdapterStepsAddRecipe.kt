package com.example.recipepool.recycleradapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R

class RecyclerAdapterStepsAddRecipe(val data: ArrayList<String>) :
    RecyclerView.Adapter<RecyclerAdapterStepsAddRecipe.ViewHolder>() {

    fun deleteItem(i:Int){
        data.removeAt(i)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_steps, parent, false)
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
        val step = v.findViewById<TextView>(R.id.step_text)
        fun bind(data: String) {
            step.text = data
        }
    }

}