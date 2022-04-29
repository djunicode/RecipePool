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
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.login
import com.example.recipepool.data.signup
import com.example.recipepool.data.token
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
    lateinit var gender:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.pBSignUp!!.visibility = View.INVISIBLE

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


        // on click sign up button
        binding.viewSignUP.setOnClickListener {

            binding.viewSignUP.isEnabled = false
            binding.pBSignUp!!.visibility = View.VISIBLE

            Log.d("clicked","Hello world")


            // if NAME is empty
            if (binding.etName.text.toString().trim().isEmpty()) {

                binding.etName.error = "Name Required"
                binding.etName.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp!!.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                return@setOnClickListener
            }

            //if PASSWORD is empty
            val password = binding.etPassword.text.toString().trim()
            if (password.isEmpty() || password.length < 6) {
                binding.etPassword.error = "Minimum 6 characters required"
                binding.etPassword.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp!!.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                return@setOnClickListener
            }

            //if PASSWORDS don't match
            val confirm_password = binding.etConfirmpass.text.toString().trim()

            if (confirm_password != password) {
                binding.etConfirmpass.error = "Passwords don,t match"
                binding.etConfirmpass.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp!!.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                return@setOnClickListener
            }

            // checking email
            val email = binding.etEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                binding.etEmail.error = "Invalid email"
                binding.etEmail.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp!!.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                return@setOnClickListener

            }

            //checking DOB
            if (binding.etDate.text.isEmpty()) {

                binding.etDate.error = "Date of Birth required"
                binding.etDate.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp!!.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                return@setOnClickListener
            }

            //checking phone number
            val number = binding.etPhone.text.toString().trim()
            if (number.isEmpty() || number.length != 10) {
                binding.etPhone.error = "Invalid phone number"
                binding.etPhone.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp!!.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                return@setOnClickListener
            }

            if(binding.radioFemale.isActivated){
                gender = "Female"
            }else if(binding.radioMale.isActivated){
                gender = "Male"
            }else{
                gender= "Other"
            }

            // retrofit builder for apis
            val list = binding.etName.text.split(" ")

            lateinit var lname:String
            if(list[1].isNotEmpty()){
               lname = list[1]
            }
            val userDataSignUp = signup(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                list[0],
                lname,
                binding.etPhone.text.toString(),
                gender,binding.etDate.text.toString(),""
            )

            val userDataLogin = login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                "",""
            )

            //handling sign up requests
            val signupRequest = rf.signup(userDataSignUp)


            val loginRequest = rf.login(userDataLogin)

            signupRequest.enqueue(object:Callback<signup>{
                override fun onResponse(call: Call<signup>, response: Response<signup>) {
                    if(response.code() == 201){
                        Log.d("signup api successful",response.message().toString())

                        //verifying user email
                        val t = token(response.body()!!.token.toString())
                        val verify = rf.emailVerify(t)
                        verify.enqueue(object :Callback<token>{
                            override fun onResponse(call: Call<token>, response: Response<token>) {
                                if(response.code() == 200){
                                    Log.d("email verified successfully",response.message().toString())

                                    //logging user in
                                    loginRequest.enqueue(object: Callback<login> {
                                        override fun onResponse(call: Call<login>, response: Response<login>) {
                                            if(response.code() == 200){
                                                Toast.makeText(this@SignUp,"Thank you for signing up",Toast.LENGTH_SHORT).show()
                                                editor.putString("access token",response.body()!!.access.toString())
                                                editor.putString("refresh token",response.body()!!.refresh.toString())
                                                editor.putString("email",binding.etEmail.text.toString())
                                                editor.apply()
                                                val intent = Intent(this@SignUp,MainActivity::class.java)
                                                binding.viewSignUP!!.isEnabled = true
                                                binding.pBSignUp!!.visibility = View.INVISIBLE
                                                startActivity(intent)
                                                finish()
                                            }
                                            else{
                                                Log.d("Signup Login response error",response.message().toString())
                                            }
                                        }
                                        override fun onFailure(call: Call<login>, t: Throwable) {
                                            Toast.makeText(this@SignUp,"Please check your email id and password",Toast.LENGTH_SHORT).show()
                                            Log.d("SignUp Login failure error",t.message.toString())
                                        }
                                    })
                                }
                                else{
                                    Log.d("email verify response error",response.message().toString())
                                }
                            }
                            override fun onFailure(call: Call<token>, t: Throwable) {
                                Log.d("email verify failure error",t.message.toString())
                            }
                        })
                    }
                    else{
                        Log.d("signup response error",response.message().toString())
                    }
                }
                override fun onFailure(call: Call<signup>, t: Throwable) {
                    binding.pBSignUp!!.visibility = View.INVISIBLE
                    Toast.makeText(this@SignUp,"Some problem please try again",Toast.LENGTH_SHORT).show()
                    Log.d("signup failure error",t.message.toString())
                    binding.viewSignUP!!.isEnabled = true
                }
            })

        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.text = sdf.format(cal.time)
    }

}