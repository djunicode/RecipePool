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
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.google
import com.example.recipepool.data.login
import com.example.recipepool.data.signup
import com.example.recipepool.data.token
import com.example.recipepool.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SignUp : AppCompatActivity() {


    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    var cal: Calendar = Calendar.getInstance()
    lateinit var gender: String

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var editor: SharedPreferences.Editor

    companion object {
        var RC_SIGN_IN = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pBSignUp.visibility = View.INVISIBLE


        // shared preferences to store user token
        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        editor = pref.edit()

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


        // email validation
        binding.etEmail.setOnFocusChangeListener { view, b ->

            if (b || binding.etEmail.text.toString().trim().isEmpty()) {
                return@setOnFocusChangeListener
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString().trim()).matches()) {

                binding.etEmail.error = "Invalid email"
                // binding.etEmail.requestFocus()
            }
        }

        // number validation
        binding.etPhone.setOnFocusChangeListener { view, b ->
            if (b) {
                return@setOnFocusChangeListener
            }
            val number = binding.etPhone.text.toString().trim()
            if (number.length != 10) {
                binding.etPhone.error = "Invalid phone number"
                // binding.etPhone.requestFocus()
            }
        }
        //name validation
        binding.etName.setOnFocusChangeListener { view, b ->
            if (b) {
                return@setOnFocusChangeListener
            }
            /* if (binding.etName.text.toString().trim().isEmpty()) {
<<<<<<< HEAD
=======

>>>>>>> e4984180cbc2beab35776d363f7445e1a5700af7
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

            Log.d("clicked", "Hello world")


            Log.d("signup", "clicked")

            var signup = true

            // if NAME is empty
            if (binding.etName.text.toString().trim().isEmpty()) {

                binding.etName.error = "Name Required"
                binding.etName.requestFocus()
                signup = false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name", "Some name error")
                // return@setOnClickListener
            }

            //if PASSWORD is empty
            val password = binding.etPassword.text.toString().trim()
            if (password.isEmpty() || password.length < 6) {
                binding.etPassword.error = "Minimum 6 characters required"
                binding.etPassword.requestFocus()
                signup = false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name", "Some name error")
                //return@setOnClickListener
            }

            //if PASSWORDS don't match
            val confirm_password = binding.etConfirmpass.text.toString().trim()

            if (confirm_password != password || confirm_password.length < 6) {
                binding.etConfirmpass.error = "Passwords don,t match"
                binding.etConfirmpass.requestFocus()
                signup = false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name", "Some name error")
                // return@setOnClickListener
            }

            // checking email
            val email = binding.etEmail.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                binding.etEmail.error = "Invalid email"
                binding.etEmail.requestFocus()
                signup = false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name", "Some name error")
                // return@setOnClickListener
            }


            //checking DOB
            if (binding.etDate.text.toString().trim().isEmpty()) {
                Log.d("error", "dob")
                binding.etDate.error = "Date of Birth required"
                binding.etDate.requestFocus()
                signup = false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name", "Some name error")
                //return@setOnClickListener
            }

            //checking phone number
            val number = binding.etPhone.text.toString().trim()
            if (number.isEmpty() || number.length != 10) {
                binding.etPhone.error = "Invalid phone number"
                binding.etPhone.requestFocus()
                signup = false
                binding.viewSignUP.isEnabled = true
                binding.pBSignUp.visibility = View.INVISIBLE
                Log.d("name", "Some name error")
                //  return@setOnClickListener
            }

            val radio = binding.radioGroup.checkedRadioButtonId
            if (radio == -1) {
                Toast.makeText(this, "Select a Gender", Toast.LENGTH_SHORT).show()
            } else {
                if (binding.radioMale.id == radio) {
                    Log.d("gender", "male")
                    gender = "Male"
                } else if (binding.radioFemale.id == radio) {
                    Log.d("gender", "female")
                    gender = "Female"
                } else if (binding.radioOthers.id == radio) {
                    Log.d("gender", "others")
                    gender = "Others"
                }
            }

            if (!signup) return@setOnClickListener

            var lname: String = "None"
            var fname: String = "None"
            if (binding.etName.text.contains(" ")) {
                val list = binding.etName.text.split(" ")
                fname = list[0]
                lname = list[1]
            } else {
                fname = binding.etName.text.toString()
            }

            //data class for sign up
            val userDataSignUp = signup(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                fname,
                lname,
                binding.etPhone.text.toString(),
                gender, binding.etDate.text.toString(), ""
            )

            //data class for login
            val userDataLogin = login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                "", ""
            )

            //handling sign up requests
            val signupRequest = rf.signup(userDataSignUp)

            //handling login request
            val loginRequest = rf.login(userDataLogin)


            signupRequest.enqueue(object : Callback<signup> {
                override fun onResponse(call: Call<signup>, response: Response<signup>) {
                    if (response.code() == 201) {
                        Log.d("signup api successful", response.message().toString())

                        //verifying user email
                        val t = response.body()!!.token.toString()
                        val verify = rf.emailVerify(t)
                        Log.d("verification token",t)
                        Log.d("error code",response.code().toString())

                        verify.enqueue(object : Callback<token> {
                            override fun onResponse(call: Call<token>, response: Response<token>) {
                                if (response.code() == 200) {
                                    Log.d("email verified successfully", response.message().toString())

                                    //logging user in
                                    loginRequest.enqueue(object : Callback<login> {
                                        override fun onResponse(call: Call<login>, response: Response<login>
                                        ) {
                                            if (response.code() == 200) {
                                                Toast.makeText(this@SignUp, "Thank you for signing up", Toast.LENGTH_SHORT).show()
                                                editor.putString("access token", response.body()!!.access.toString())
                                                editor.putString("refresh token", response.body()!!.refresh.toString())
                                                editor.putString("email", binding.etEmail.text.toString())
                                                editor.apply()
                                                val intent = Intent(this@SignUp, MainActivity::class.java)
                                                binding.viewSignUP.isEnabled = true
                                                binding.pBSignUp.visibility = View.INVISIBLE
                                                startActivity(intent)
                                                finish()
                                            } else {
                                                Toast.makeText(this@SignUp, "Some error occurred please try after some time", Toast.LENGTH_SHORT).show()
                                                Log.d("Signup Login response error", response.message().toString())
                                                binding.viewSignUP.isEnabled = true
                                                binding.pBSignUp.visibility = View.INVISIBLE
                                            }
                                        }

                                        override fun onFailure(call: Call<login>, t: Throwable) {
                                            Toast.makeText(this@SignUp, "Please check your email id and password", Toast.LENGTH_SHORT).show()
                                            Log.d("SignUp Login failure error", t.message.toString())
                                            binding.viewSignUP.isEnabled = true
                                            binding.pBSignUp.visibility = View.INVISIBLE
                                        }
                                    })
                                } else {
                                    Log.d("email verify response error", response.message().toString())
                                    Toast.makeText(this@SignUp, "Some error occurred please try after some time", Toast.LENGTH_SHORT).show()
                                    binding.viewSignUP.isEnabled = true
                                    binding.pBSignUp.visibility = View.INVISIBLE
                                }
                            }
                            override fun onFailure(call: Call<token>, t: Throwable) {
                                Log.d("email verify failure error", t.message.toString())
                                Toast.makeText(this@SignUp, "Some error occurred please try after some time", Toast.LENGTH_SHORT).show()
                                Log.d("Signup Login response error", response.message().toString())
                                binding.viewSignUP.isEnabled = true
                                binding.pBSignUp.visibility = View.INVISIBLE
                            }
                        })
                    } else {
                        Log.d("signup response error", response.message().toString())
                        Toast.makeText(this@SignUp, "Email id has been already used", Toast.LENGTH_SHORT).show()
                        binding.viewSignUP.isEnabled = true
                        binding.pBSignUp.visibility = View.INVISIBLE                    }
                }

                override fun onFailure(call: Call<signup>, t: Throwable) {
                    binding.pBSignUp.visibility = View.INVISIBLE
                    Toast.makeText(this@SignUp, "Some problem please try again", Toast.LENGTH_SHORT).show()
                    Log.d("signup failure error", t.message.toString())
                    binding.viewSignUP.isEnabled = true
                }
            })

        }

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }

        binding.btSignUpGoogle.setOnClickListener {
            binding.pBSignUp.visibility = View.VISIBLE

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken("248934162242-8vijpcs6fqh7ja9hessahu7mvkhcsm12.apps.googleusercontent.com")
//            21048189619-v974lmaf367qu7q7ccaj2f701vigqf4u.apps.googleusercontent.com
                .requestIdToken("21048189619-4n65kvscmm2gk0amf5gks54faro9ti7h.apps.googleusercontent.com")
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            signUp()
        }

    }

    private fun signUp() {
        val signInIntent = Intent(mGoogleSignInClient.signInIntent)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Login.RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val user = completedTask.getResult(ApiException::class.java)
            Log.d("google sign up token",user.idToken.toString())

            val tokenData = google("","",user.idToken.toString())

            val googleRequest = rf.google(tokenData)
            googleRequest.enqueue(object :Callback<google>{
                override fun onResponse(call: Call<google>, response: Response<google>) {
                    if(response.code() == 200){
                        Toast.makeText(this@SignUp, "Welcome to Recipe Pool", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@SignUp, MainActivity::class.java)
                        editor.putString("access token", response.body()!!.access.toString())
                        editor.putString("refresh token", response.body()!!.refresh.toString())
                        editor.apply()
                        binding.btSignUpGoogle.isEnabled = true
                        binding.pBSignUp.visibility = View.INVISIBLE
                        startActivity(intent)
                        finish()
                    }
                    else{
                        Toast.makeText(this@SignUp, "Please try againg later", Toast.LENGTH_SHORT)
                            .show()
                        binding.btSignUpGoogle.isEnabled = true
                        binding.pBSignUp.visibility = View.INVISIBLE
                    }
                }

                override fun onFailure(call: Call<google>, t: Throwable) {
                    binding.btSignUpGoogle.isEnabled = true
                    binding.pBSignUp.visibility = View.INVISIBLE
                    Toast.makeText(this@SignUp, "Please try again later", Toast.LENGTH_SHORT).show()
                }
            })
        }
        catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Message", e.toString())
            Toast.makeText(this,"Please sign in", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateDateInView() {
        val myFormat = "yyyy-MM-dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etDate.text = sdf.format(cal.time)
    }


    private fun firebaseAuthWithGoogle(idToken: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
                    val user = mAuth.currentUser
//                    signOut()
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Log.d("failure", "firebase failure")
                }
            }
    }

}