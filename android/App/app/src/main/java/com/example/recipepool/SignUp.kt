package com.example.recipepool

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.widget.DatePicker
import com.example.recipepool.databinding.ActivitySignUpBinding
import java.text.SimpleDateFormat
import java.util.*

class SignUp : AppCompatActivity() {

    private lateinit var binding : ActivitySignUpBinding
    var cal: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)

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

        binding.btSignup.setOnClickListener {

            // if NAME is empty
            if (binding.etName.text.toString().trim().isEmpty()){

                binding.etName.error = "Name Required"
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            //if PASSWORD is empty
            val password = binding.etPassword.text.toString().trim()
            if (password.isEmpty() || password.length<6){
                binding.etPassword.error = "Minimum 6 characters required"
                binding.etPassword.requestFocus()
                return@setOnClickListener
            }

            //if PASSWORDS don't match
            val confirm_password = binding.etConfirmpass.text.toString().trim()

            if (password != password){
                binding.etConfirmpass.error = "Passwords don,t match"
                binding.etConfirmpass.requestFocus()
                return@setOnClickListener
            }

            // checking email
            val email = binding.etEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

                binding.etEmail.error = "Invalid email"
                binding.etEmail.requestFocus()
                return@setOnClickListener

            }

            //checking DOB
            if (binding.etDate.text.isEmpty()){

                binding.etDate.error = "Date of Birth required"
                binding.etDate.requestFocus()
                return@setOnClickListener
            }

            //checking phone number
            val number = binding.etPhone.text.toString().trim()
            if (number.isEmpty() || number.length!=10){
                binding.etPhone.error = "Invalid phone number"
                binding.etPhone.requestFocus()
                return@setOnClickListener
            }

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

        }

        binding.tvLogin.setOnClickListener {

            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }


        setContentView(binding.root)
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.text = sdf.format(cal.time)
    }

}