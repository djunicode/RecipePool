package com.example.recipepool.data

data class AddRecipe(
    val cuisine:String?,
    val createdBy:String?,
    val label:String?,
    val instructions:String?,
    val steps:String?,
    val totalTime:String?,
    val url:String?,
    val image:String?,
    val healthLabels:String?,
    val totalNutrients:String?,
    val calories:String?,
    val cuisineType:String?,
    val mealType:String?,
    val dishType:String?,
    val likes:String?,
    val missingIngredients:String?,
    val ingredient_list:List<Ingredients>
)
