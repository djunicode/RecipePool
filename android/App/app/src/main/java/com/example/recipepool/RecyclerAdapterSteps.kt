package com.example.recipepool

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.databinding.LayoutStepsBinding

class RecyclerAdapterSteps(private var data: List<String>) :
    RecyclerView.Adapter<RecyclerAdapterSteps.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutStepsBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                binding.stepText.text = this
                if(binding.stepCheck.isActivated) {
                    binding.stepText.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutStepsBinding) : RecyclerView.ViewHolder(binding.root)
}