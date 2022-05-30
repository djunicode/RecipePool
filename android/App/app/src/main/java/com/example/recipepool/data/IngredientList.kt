package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

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