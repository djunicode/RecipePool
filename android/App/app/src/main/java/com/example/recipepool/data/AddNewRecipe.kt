package com.example.recipepool.data

import okhttp3.MultipartBody
import okhttp3.RequestBody


data class AddNewRecipe(
    val calories: Int,
    val cuisine: String?,
    val cuisineType: String?,
    val dishType: String?,
    val healthLabels: String?,
    val image: RequestBody?,
    val ingredient_list: List<AddRecipeIngredient>?,
    val instructions: String?,
    val label: String?,
    val likes: Int,
    val mealType: String?,
    val missingIngredients: String?,
    val steps_list: List<AddRecipeSteps>,
    val totalNutrients: String?,
    val totalTime: String?,
)
