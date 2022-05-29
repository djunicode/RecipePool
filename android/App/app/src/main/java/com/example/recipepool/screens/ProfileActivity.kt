package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Favourite
import com.example.recipepool.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.profileToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.layoutProfileAddRecipe.setOnClickListener {
            val intent = Intent(this, AddRecipe::class.java)
            startActivity(intent)
        }

        sharedPreferences = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("email", null).toString()
        Log.d("Email", userEmail)

        fetchFavourite()
    }

    private fun fetchFavourite() {
        val favourite = rf.getFavourite()

        favourite.enqueue(object : Callback<ArrayList<Favourite>> {
            override fun onResponse(
                call: Call<ArrayList<Favourite>>,
                response: Response<ArrayList<Favourite>>
            ) {
                if(response.isSuccessful) {
                    Log.e("Favourite", response.message().toString() + " " + response.code().toString())
                    Log.e("Favourite Code", response.code().toString())
                    val result = response.body()
                    Log.e("Favourite Result", result.toString())
                    Log.d("Favourite", "Successful")
                }


                if(response.code() == 200) {
                    Log.e("Favourite", response.message().toString() + " " + response.code().toString())
                    Log.e("Favourite Code", response.code().toString())
                    val result = response.body()
                    Log.e("Favourite Result", result.toString())
                    Log.d("Favourite", "Successful")
                }
            }

            override fun onFailure(call: Call<ArrayList<Favourite>>, t: Throwable) {
                Log.e("Favourite Fetch", t.message.toString())
            }

        })
    }
}