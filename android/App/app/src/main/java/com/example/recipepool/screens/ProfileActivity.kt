package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.recipepool.databinding.ActivityProfileBinding

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
        val userName = sharedPreferences.getString("username", null).toString()
        Log.d("Email", userEmail)

        binding.textProfileName.setText(userName)
        binding.textProfileUsername.setText(userEmail)

        binding.profileSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}