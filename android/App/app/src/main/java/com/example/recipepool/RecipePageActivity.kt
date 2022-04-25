package com.example.recipepool

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipepool.databinding.ActivityRecipePageBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeName = intent.getStringExtra("recipe_name")
        binding.textRecipeName.text = recipeName

        binding.recyclerViewIngredients.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = RecyclerAdapterIngredients(ingredientList)
        }

        binding.startCookingButton.setOnClickListener {
            binding.startCookingButton.visibility = View.GONE
            binding.textSteps.visibility = View.VISIBLE
            binding.recyclerViewSteps.visibility = View.VISIBLE
            binding.imageBack.visibility = View.VISIBLE
            binding.textRecipeName.setCompoundDrawables(null, null, ContextCompat.getDrawable(this.baseContext, R.drawable.ic_red), null)

            binding.recyclerViewSteps.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = RecyclerAdapterSteps(stepList)
            }
        }
    }
}