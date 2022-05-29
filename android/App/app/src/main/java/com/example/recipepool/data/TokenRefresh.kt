package com.example.recipepool.data

import com.google.gson.annotations.SerializedName

data class TokenRefresh(
    @SerializedName("access")
    val access: String?,
    @SerializedName("refresh")
    val refresh: String?
)