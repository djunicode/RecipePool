package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

data class Recipe(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("cuisine")
    val cuisine: Cuisine,
    @SerializedName("createdBy")
    val createdBy: Int?,
    @SerializedName("label")
    val recipeName: String?,
    @SerializedName("instructions")
    val instructions: String?,
    @SerializedName("steps_list")
    val stepsList: List<Steps>,
    @SerializedName("totalTime")
    val totalTime: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("healthLabels")
    val healthLabels: String?,
    @SerializedName("totalNutrients")
    val totalNutrients: String?,
    @SerializedName("calories")
    val calories: Int?,
    @SerializedName("cuisineType")
    val cuisineType: Int?,
    @SerializedName("mealType")
    val mealType: String?,
    @SerializedName("dishType")
    val dishType: String?,
    @SerializedName("likes")
    val likes: Int?,
    @SerializedName("missingIngredients")
    val missingIngredients: String?,
    @SerializedName("ingredient_list")
    val ingredientList: List<IngredientList>
) {
    class Cuisine(
        @SerializedName("cuisine_name")
        val cuisineName: String?,
        @SerializedName("images")
        val images: String?,
        @SerializedName("dishType")
        val likes: Int?
    )

    data class Steps(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("steps")
        val step: String?,
        @SerializedName("recipe")
        val recipeId: Int?
    )

    data class IngredientList(
        @SerializedName("id")
        val id: Int?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("quantity")
        val quantity: Double?,
        @SerializedName("recipe")
        val recipeId: Int?,
        @SerializedName("ingredient")
        val ingredient: Int?,
    )


}