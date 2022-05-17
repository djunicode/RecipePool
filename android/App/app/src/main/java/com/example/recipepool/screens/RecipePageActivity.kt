package com.example.recipepool.screens

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipepool.IngredientList
import com.example.recipepool.R
import com.example.recipepool.databinding.ActivityRecipePageBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterIngredients
import com.example.recipepool.recycleradapter.RecyclerAdapterRecipeImages
import com.example.recipepool.recycleradapter.RecyclerAdapterSteps

class RecipePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipePageBinding

    private val ingredientList: List<IngredientList> = arrayListOf(
        IngredientList("Flour (All Purpose)", "5 cups"),
        IngredientList("Chocolate", "2 slabs"),
        IngredientList("Vanilla Extract", "2 tea spoons"),
        IngredientList("Eggs", "3")
    )

    private val stepList: List<String> = arrayListOf(
        "Crack eggs in a bowl",
        "Melt Chocolate using double boiler . Keep a vessel with chopped compound over a boiling water vessel",
        "Bake the cookies at 200 degrees for 20 minutes")

    private val imageList: List<Int> = arrayListOf(R.drawable.ic_account, R.drawable.ic_fav, R.drawable.ic_home)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarRecipePage)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val recipeName = intent.getStringExtra("recipe_name")
        binding.textRecipeName.text = recipeName

        binding.recyclerViewIngredients.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = RecyclerAdapterIngredients(ingredientList)
        }

        binding.carouselViewRecipeImage.adapter = RecyclerAdapterRecipeImages(imageList)
        binding.carouselViewRecipeImage.apply {
            set3DItem(true)
            setAlpha(true)
            setIntervalRatio(1f)
//            setFlat(true)
        }

        binding.startCookingButton.setOnClickListener {
            binding.startCookingButton.visibility = View.GONE
            binding.textSteps.visibility = View.VISIBLE
            binding.recyclerViewSteps.visibility = View.VISIBLE
            binding.textRecipeName.setCompoundDrawables(null, null, ContextCompat.getDrawable(this.baseContext,
                R.drawable.ic_red
            ), null)

            binding.recyclerViewSteps.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = RecyclerAdapterSteps(stepList)
            }
        }

        binding.imageFavourite.setOnClickListener {
            binding.imageFavourite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24))
        }
    }
}