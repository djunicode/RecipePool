package com.example.recipepool.screens

import android.app.DatePickerDialog
import android.content.SharedPreferences
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.recipepool.R
import com.example.recipepool.apis.RetrofitApi
import com.example.recipepool.data.signup
import com.example.recipepool.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    var cal: Calendar = Calendar.getInstance()
    var baseUrl = "https://therecipepool.pythonanywhere.com/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // shared preferences to store user token
        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()

        //code for setting status bar white
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        binding.etDate.setOnClickListener {
            DatePickerDialog(
                this@SignUp,
                dateSetListener,
                // set DatePickerDialog to point to today's date when it loads up
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // on click sing up button
        binding.btSignup.setOnClickListener {

            // if NAME is empty
            if (binding.etName.text.toString().trim().isEmpty()) {

                binding.etName.error = "Name Required"
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            //if PASSWORD is empty
            val password = binding.etPassword.text.toString().trim()
            if (password.isEmpty() || password.length < 6) {
                binding.etPassword.error = "Minimum 6 characters required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            //if PASSWORDS don't match
            val confirm_password = binding.etConfirmpass.text.toString().trim()

            if (password != password) {
                binding.etConfirmpass.error = "Passwords don,t match"
                binding.etConfirmpass.requestFocus()
                return@setOnClickListener
            }

            // checking email
            val email = binding.etEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                binding.etEmail.error = "Invalid email"
                binding.etEmail.requestFocus()
                return@setOnClickListener

            }

            //checking DOB
            if (binding.etDate.text.isEmpty()) {

                binding.etDate.error = "Date of Birth required"
                binding.etDate.requestFocus()
                return@setOnClickListener
            }

            //checking phone number
            val number = binding.etPhone.text.toString().trim()
            if (number.isEmpty() || number.length != 10) {
                binding.etPhone.error = "Invalid phone number"
                binding.etPhone.requestFocus()
                return@setOnClickListener
            }

            // retrofit builder for apis
            val rf = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitApi::class.java)

            val userData = signup(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                binding.etName.text.toString(),
                "",""
            )

            //handling sign up requests
            val signupRequest = rf.signup(userData)

            signupRequest.enqueue(object:Callback<signup>{
                override fun onResponse(call: Call<signup>, response: Response<signup>) {
                    if(response.code() == 200){
                        Toast.makeText(this@SignUp,"Thank you for signing up",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUp,MainActivity::class.java)
                        editor.putString("token",response.body()!!.token.toString())
                        editor.putString("email",response.body()!!.email.toString())
                        editor.putString("name",binding.etName.text.toString())
                        editor.apply()
                        startActivity(intent)
                        finish()
                    }
                }
                override fun onFailure(call: Call<signup>, t: Throwable) {
                    Log.d("Some sign up error occurred",t.message.toString())
                }
            })

        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.text = sdf.format(cal.time)
    }

}