package com.example.recipepool.data

data class ResponseNewRecipe(
    val calories: Int,
    val createdBy: Int,
    val cuisine: Cuisine,
    val cuisineType: String,
    val dishType: String,
    val healthLabels: String,
    val id: Int,
    val image: Any,
    val ingredient_list: List<Ingredient>,
    val instructions: String,
    val label: String,
    val likes: Int,
    val mealType: String,
    val missingIngredients: String,
    val steps_list: List<Steps>,
    val totalNutrients: String,
    val totalTime: String,
    val url: Any
) {
    companion object {}
    data class Cuisine(
        val cuisine_name: String,
        val image: String,
        val likes: Int
    )

    data class Ingredient(
        val id: Int,
        val ingredient: Int,
        val name: String,
        val quantity: Double,
        val recipe: Int
    )
}