package com.example.recipepool.screens

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
        binding.pBSignUp.visibility = View.INVISIBLE

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



        if (binding.radioFemale.isChecked){
            Log.d("gender","female")
        }
        if (binding.radioMale.isChecked){
            Log.d("gender","male")
        }
        if (binding.radioOthers.isChecked){
            Log.d("gender","others")
        }


        // email validation
        binding.etEmail.setOnFocusChangeListener { view, b ->

            if (b ||binding.etEmail.text.toString().trim().isEmpty()){
                return@setOnFocusChangeListener
            }
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()) {

                    binding.etEmail.error = "Invalid email"
                   // binding.etEmail.requestFocus()
                }
        }

        // number validation
        binding.etPhone.setOnFocusChangeListener { view, b ->
            if (b){
                return@setOnFocusChangeListener
            }
            val number = binding.etPhone.text.toString().trim()
            if ( number.length != 10  ) {
                binding.etPhone.error = "Invalid phone number"
               // binding.etPhone.requestFocus()
            }
        }
        //name validation
        binding.etName.setOnFocusChangeListener { view, b ->
            if (b){
                return@setOnFocusChangeListener
            }
           /* if (binding.etName.text.toString().trim().isEmpty()) {

                binding.etName.error = "Name Required"
              //  binding.etName.requestFocus()
            }*/

        }

        //pasword validation
        binding.etPassword.setOnFocusChangeListener { view, b ->
            val password = binding.etPassword.text.toString().trim()
            if (b || password.isEmpty()) return@setOnFocusChangeListener

            if (password.length < 6) {
                binding.etPassword.error = "Minimum 6 characters required"
               // binding.etPassword.requestFocus()

            }

        }

        //confirm password
        binding.etConfirmpass.setOnFocusChangeListener { view, b ->
            if (b) return@setOnFocusChangeListener
            val password = binding.etPassword.text.toString().trim()
            val confirm_password = binding.etConfirmpass.text.toString().trim()

            if (password != confirm_password) {
                binding.etConfirmpass.error = "Passwords don,t match"
               // binding.etConfirmpass.requestFocus()

            }
        }

        //DOB

        binding.etDate.setOnFocusChangeListener { view, b ->
            if (b) return@setOnFocusChangeListener
            /*if ( binding.etDate.text.toString().trim().isEmpty()) {
                binding.etDate.error = "Date of Birth required"
              //  binding.etDate.requestFocus()

            }*/

        }


        // on click sign up button
        binding.viewSignUP.setOnClickListener {

            binding.viewSignUP.isEnabled = false
            binding.pBSignUp.visibility = View.VISIBLE
            val radio = binding.radioGroup.checkedRadioButtonId
            val gender:String
            if (radio == -1) {
                Toast.makeText(this, "Select a Gender", Toast.LENGTH_SHORT).show()
            }
            else{
                if (binding.radioMale.id == radio){
                    Log.d("gender", "male")
                    gender = "Male"
                }
                else if (binding.radioFemale.id == radio){
                    Log.d("gender", "female")
                    gender = "Female"
                }
                else if (binding.radioOthers.id == radio){
                    Log.d("gender", "others")
                    gender = "Others"
                }
            }





            Log.d("clicked","Hello world")


            Log.d("signup","clicked")

            var signup = true

            // if NAME is empty
            if (binding.etName.text.toString().trim().isEmpty()) {

                binding.etName.error = "Name Required"
                binding.etName.requestFocus()
                signup =false

                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name","Some name error")
               // return@setOnClickListener
            }

            //if PASSWORD is empty
            val password = binding.etPassword.text.toString().trim()
            if (password.isEmpty() || password.length < 6) {
                binding.etPassword.error = "Minimum 6 characters required"
                binding.etPassword.requestFocus()

                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                signup = false
                Log.d("name","Some name error")
                //return@setOnClickListener
            }

            //if PASSWORDS don't match
            val confirm_password = binding.etConfirmpass.text.toString().trim()

            if (confirm_password != password || confirm_password.length<6) {
                binding.etConfirmpass.error = "Passwords don,t match"
                binding.etConfirmpass.requestFocus()

                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                signup = false
                Log.d("name","Some name error")
               // return@setOnClickListener
            }

            // checking email
            val email = binding.etEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                binding.etEmail.error = "Invalid email"
                binding.etEmail.requestFocus()
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                signup = false
                Log.d("name","Some name error")
               // return@setOnClickListener

            }


            //checking DOB
            if ( binding.etDate.text.toString().trim().isEmpty()) {
                Log.d("error","dob")
                binding.etDate.error = "Date of Birth required"
                binding.etDate.requestFocus()
                signup=false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name","Some name error")
                //return@setOnClickListener
            }

            //checking phone number
            val number = binding.etPhone.text.toString().trim()
            if (number.isEmpty() || number.length != 10) {
                binding.etPhone.error = "Invalid phone number"
                binding.etPhone.requestFocus()
                signup =false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name","Some name error")
              //  return@setOnClickListener
            }

                if (!signup) return@setOnClickListener
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
                "None",""
            )

            //handling sign up requests
            val signupRequest = rf.signup(userData)

            print(binding.etEmail.text.toString())
            print(binding.etName.text.toString())
            print(binding.etPassword.text.toString())
            print("None")

            signupRequest.enqueue(object:Callback<signup>{
                override fun onResponse(call: Call<signup>, response: Response<signup>) {
                    if(response.code() == 201){
                        Toast.makeText(this@SignUp,"Thank you for signing up",Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@SignUp,MainActivity::class.java)
                        editor.putString("token",response.body()!!.token.toString())
                        editor.putString("email",response.body()!!.email.toString())
                        editor.putString("name",binding.etName.text.toString())
                        editor.apply()
                        binding.viewSignUP.isEnabled = true
                        binding.pBSignUp.visibility = View.INVISIBLE
                        Log.d("response",response.message().toString())
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Log.d("response",response.message().toString())
                    }
                }

                override fun onFailure(call: Call<signup>, t: Throwable) {
                    binding.pBSignUp.visibility = View.INVISIBLE
                    Toast.makeText(this@SignUp,"Some problem please try again",Toast.LENGTH_SHORT).show()
                    Log.d("Some sign up error occurred",t.message.toString())
                    binding.viewSignUP.isEnabled = true
                }
            })

        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

    }

    fun updateDateInView() {
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.text = sdf.format(cal.time)
    }

}