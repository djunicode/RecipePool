package com.example.recipepool.recycleradapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.databinding.LayoutRecipeImageBinding

class RecyclerAdapterRecipeImages(private var data: List<String>, private val callbackImage: CallbackImage) :
    RecyclerView.Adapter<RecyclerAdapterRecipeImages.ViewHolder>() {
    private lateinit var handler: Handler
    private var isDoubleClicked = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutRecipeImageBinding.inflate(LayoutInflater.from(parent.context),
            parent, false)
        handler = Handler(Looper.getMainLooper())
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(data[position]) {
                if(this != null) {
                    Glide.with(itemView.context)
                        .load("https://therecipepool.pythonanywhere.com$this")
                        .fitCenter()
                        .into(binding.recipeImage)
                }
                else {
                    Glide.with(itemView.context)
                        .load(R.drawable.ic_launcher_background).
                        into(binding.recipeImage)
                }
            }

            binding.recipeImage.setOnClickListener {
                val runnable = Runnable {
                    isDoubleClicked = false
                }

                if (isDoubleClicked) {
                    callbackImage.resultCallback("image_like")
                    isDoubleClicked = false
                    handler.removeCallbacks(runnable)
                }
                else {
                    isDoubleClicked = true
                    handler.postDelayed(runnable, 500)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(val binding: LayoutRecipeImageBinding) : RecyclerView.ViewHolder(binding.root)

    interface CallbackImage {
        fun resultCallback(message: String)
    }
}