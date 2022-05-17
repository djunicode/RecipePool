package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

data class Trending(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("label")
    val label: String?,
    @SerializedName("instructions")
    val instructions: String?,
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
    val calories: Double?,
    @SerializedName("cuisineType")
    val cuisineType: String?,
    @SerializedName("mealType")
    val mealType: String?,
    @SerializedName("dishType")
    val dishType: String?,
    @SerializedName("likes")
    val likes: Int?,
    @SerializedName("missingIngredients")
    val missingIngredients: String?,
    @SerializedName("createdBy")
    val createdBy: Int?
)