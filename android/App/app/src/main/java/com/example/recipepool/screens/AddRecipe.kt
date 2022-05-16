package com.example.recipepool.screens

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipepool.databinding.ActivityAddRecipeBinding

class AddRecipe : AppCompatActivity() {

    private lateinit var binding:ActivityAddRecipeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
    }
}