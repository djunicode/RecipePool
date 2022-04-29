package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.recipepool.R
import com.example.recipepool.apis.RetrofitApi
import com.example.recipepool.data.login
import com.example.recipepool.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    var baseUrl = "https://therecipepool.pythonanywhere.com/"


    // start function to switch between login and main
    override fun onStart() {
        super.onStart()

        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)

        val token = pref.getString("access token",null)
        if(!token.isNullOrEmpty()){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            Log.d("token in data",pref.getString("token",null).toString())
            Log.d("email in data",pref.getString("email",null).toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pBLogin!!.visibility = View.INVISIBLE


        // shared preferences to store user token
        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        //code for setting status bar white
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // code for login to sign up intent
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this,SignUp::class.java)
            startActivity(intent)
        }

        //email error check
        binding.etEmail.setOnFocusChangeListener { view, b ->
            if (b)return@setOnFocusChangeListener
            if (binding.etEmail.text.toString().trim().isEmpty()){

                binding.etEmail.error = "Email Required"
                //binding.etEmail.requestFocus()

            }
        }

        //password error check

        binding.etPassword.setOnFocusChangeListener { view, b ->
            if (b)return@setOnFocusChangeListener
            if (binding.etPassword.text.toString().trim().isEmpty()){

                binding.etPassword.error = "Password Required"
                //binding.etPassword.requestFocus()

            }

        }

        binding.btLogin.setOnClickListener {

            binding.btLogin.isEnabled = false

            binding.pBLogin!!.visibility = View.VISIBLE

            if (binding.etEmail.text.toString().trim().isEmpty()){
                binding.etEmail.error = "Email Required"
                binding.etEmail.requestFocus()
                binding.btLogin.isEnabled = true
                binding.pBLogin!!.visibility = View.INVISIBLE
                return@setOnClickListener
            }

            if (binding.etPassword.text.toString().trim().isEmpty()){
                binding.etPassword.error = "Password Required"
                binding.etPassword.requestFocus()
                binding.btLogin.isEnabled = true
                binding.pBLogin!!.visibility = View.INVISIBLE
                return@setOnClickListener
            }

            // retrofit builder for apis
            val rf = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitApi::class.java)

            val userData = login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                "",""
            )


            //handling api requests
            val loginRequest = rf.login(userData)

            loginRequest.enqueue(object: Callback<login> {
                override fun onResponse(call: Call<login>, response: Response<login>) {
                    if(response.code() == 200){
                        Toast.makeText(this@Login,"Welcome to Recipe Pool", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Login,MainActivity::class.java)
                        editor.putString("access token",response.body()!!.access.toString())
                        editor.putString("refresh token",response.body()!!.refresh.toString())
                        editor.putString("email",binding.etEmail.text.toString())
                        editor.apply()
                        binding.btLogin.isEnabled = true
                        binding.pBLogin!!.visibility = View.INVISIBLE
                        startActivity(intent)
                        finish()
                    }
                }
                override fun onFailure(call: Call<login>, t: Throwable) {
                    binding.pBLogin!!.visibility = View.INVISIBLE
                    Toast.makeText(this@Login,"Please check your email id or sign up",Toast.LENGTH_SHORT).show()
                    binding.btLogin.isEnabled = true
                    Log.d("Some sign up error occurred",t.message.toString())
                }
            })
        }


    }
}