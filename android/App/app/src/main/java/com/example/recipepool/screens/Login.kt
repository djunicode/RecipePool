package com.example.recipepool.screens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.recipepool.R
import com.example.recipepool.databinding.ActivityLoginBinding

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        binding.btLogin.setOnClickListener {

            if (binding.etName.text.toString().trim().isEmpty()){

                binding.etName.error = "Name Required"
                binding.etName.requestFocus()
                return@setOnClickListener
            }
            if (binding.etPassword.text.toString().trim().isEmpty()){

                binding.etPassword.error = "Password Required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }



        setContentView(binding.root)
    }
}