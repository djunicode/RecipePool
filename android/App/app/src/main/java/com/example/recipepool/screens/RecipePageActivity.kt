package com.example.recipepool.screens

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Favourite
import com.example.recipepool.data.IngredientList
import com.example.recipepool.data.TokenRefresh
import com.example.recipepool.databinding.ActivityRecipePageBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterIngredients
import com.example.recipepool.recycleradapter.RecyclerAdapterRecipeImages
import com.example.recipepool.recycleradapter.RecyclerAdapterSteps
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipePageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

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

        sharedPreferences = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", null).toString()
        Log.d("Email", userEmail)

        editor = sharedPreferences.edit()

        val recipeId = intent.getStringExtra("recipe_id")?.toInt()
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
            if(binding.imageFavourite.drawable.constantState == ContextCompat.getDrawable(this, R.drawable.ic_fav)?.constantState) {
                binding.imageFavourite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24))
            }
            else {
                binding.imageFavourite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fav))
            }

        }

        binding.imageBookmark.setOnClickListener {
            if(binding.imageBookmark.drawable.constantState == ContextCompat.getDrawable(this, R.drawable.ic_vector_bookmark)?.constantState) {
                postFavourite(recipeId)
            }
            else {
                deleteFavourite(recipeId)
            }
        }
    }

    private fun postFavourite(recipeId: Int?) {
        Log.d("recipeId", recipeId.toString())
        val tokenData = TokenRefresh("", sharedPreferences.getString("refresh token", null))
        Log.d("Token", tokenData.refresh.toString())
        val accessToken = rf.refreshToken(tokenData)

        accessToken.enqueue(object : Callback<TokenRefresh> {
            override fun onResponse(call: Call<TokenRefresh>, response: Response<TokenRefresh>) {
                if(response.code() == 200) {
                    val access = response.body()?.access.toString()
                    val refresh = response.body()?.refresh.toString()
                    Log.d("Access", access)
                    Log.d("Refresh", refresh)
                    editor.putString("access token", access)
                    editor.putString("refresh token", refresh)
                    editor.apply()

                    val markFavourite = rf.postFavourite("Bearer $access", recipeId)

                    markFavourite.enqueue(object : Callback<Favourite> {
                        override fun onResponse(call: Call<Favourite>, response: Response<Favourite>) {
                            if (response.code() == 200) {
                                Log.d("Favourite url","response.raw().request().url(): " + response.raw().request().url())
                                Log.d("Favourite data", response.body().toString())
                                binding.imageBookmark.setImageDrawable(ContextCompat.getDrawable(this@RecipePageActivity, R.drawable.ic_baseline_bookmark_added_24))
                                Toast.makeText(this@RecipePageActivity, "Added to Favourites", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Log.d("error", response.message())
                                Log.d("error", response.code().toString())
                                Log.d("url", "response.raw().request().url(): " + response.raw().request().url())
                            }
                        }

                        override fun onFailure(call: Call<Favourite>, t: Throwable) {
                            Log.d("Error", t.message.toString())
                        }

                    })

                }
                else {
                    Log.d("error",response.message())
                    Log.d("error",response.code().toString())
                    Log.d("url","response.raw().request().url(): " + response.raw().request().url())
                }
            }

            override fun onFailure(call: Call<TokenRefresh>, t: Throwable) {
                Log.d("Access Error", t.message.toString())
            }

        })
    }

    private fun deleteFavourite(recipeId: Int?) {
        Log.d("recipeId", recipeId.toString())
        val tokenData = TokenRefresh("", sharedPreferences.getString("refresh token", null))
        Log.d("Token", tokenData.refresh.toString())
        val accessToken = rf.refreshToken(tokenData)

        accessToken.enqueue(object : Callback<TokenRefresh> {
            override fun onResponse(call: Call<TokenRefresh>, response: Response<TokenRefresh>) {
                if(response.code() == 200) {
                    val access = response.body()?.access.toString()
                    val refresh = response.body()?.refresh.toString()
                    Log.d("Access", access)
                    Log.d("Refresh", refresh)
                    editor.putString("access token", access)
                    editor.putString("refresh token", refresh)
                    editor.apply()

                    val deleteFavourite = rf.deleteFavourite("Bearer $access", recipeId)

                    deleteFavourite.enqueue(object : Callback<Favourite> {
                        override fun onResponse(call: Call<Favourite>, response: Response<Favourite>) {
                            if (response.code() == 204) {
                                Log.d("Favourite url","response.raw().request().url(): " + response.raw().request().url())
                                Log.d("Favourite data", response.body().toString())
                                binding.imageBookmark.setImageDrawable(ContextCompat.getDrawable(this@RecipePageActivity, R.drawable.ic_vector_bookmark))
                                Toast.makeText(this@RecipePageActivity, "Deleted from Favourites", Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Log.d("error", response.message())
                                Log.d("error", response.code().toString())
                                Log.d("url", "response.raw().request().url(): " + response.raw().request().url())
                            }
                        }

                        override fun onFailure(call: Call<Favourite>, t: Throwable) {
                            Log.d("Error", t.message.toString())
                        }

                    })

                }
                else {
                    Log.d("error",response.message())
                    Log.d("error",response.code().toString())
                    Log.d("url","response.raw().request().url(): " + response.raw().request().url())
                }
            }

            override fun onFailure(call: Call<TokenRefresh>, t: Throwable) {
                Log.d("Access Error", t.message.toString())
            }

        })
    }
}