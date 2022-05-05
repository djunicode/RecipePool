package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.google
import com.example.recipepool.data.login
import com.example.recipepool.databinding.ActivityLoginBinding
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


class Login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var mAuth: FirebaseAuth


    companion object {
        var RC_SIGN_IN = 100
    }

    // start function to switch between login and main
    override fun onStart() {
        super.onStart()

        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        val token = pref.getString("refresh token", null)

        // Check if user is signed in (non-null) and update UI accordingly.
        if (!token.isNullOrEmpty()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.d("token in data", pref.getString("token", null).toString())
            Log.d("email in data", pref.getString("email", null).toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.pBLogin.visibility = View.INVISIBLE


        // shared preferences to store user token
        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        editor = pref.edit()

        //code for setting status bar white
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // code for login to sign up intent
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        //email error check
        binding.etEmail.setOnFocusChangeListener { view, b ->
            if (b) return@setOnFocusChangeListener
            if (binding.etEmail.text.toString().trim().isEmpty()) {
                binding.etEmail.error = "Email Required"
                //binding.etEmail.requestFocus()

            }
        }

        //password error check

        binding.etPassword.setOnFocusChangeListener { view, b ->
            if (b) return@setOnFocusChangeListener
            if (binding.etPassword.text.toString().trim().isEmpty()) {

                binding.etPassword.error = "Password Required"
                //binding.etPassword.requestFocus()

            }

        }

        binding.btLogin.setOnClickListener {

            binding.pBLogin.visibility = View.VISIBLE

            if (binding.etEmail.text.toString().trim().isEmpty()) {
                binding.etEmail.error = "Email Required"
                binding.etEmail.requestFocus()
                binding.btLogin.isEnabled = true
                binding.pBLogin.visibility = View.INVISIBLE
                return@setOnClickListener
            }

            if (binding.etPassword.text.toString().trim().isEmpty()) {
                binding.etPassword.error = "Password Required"
                binding.etPassword.requestFocus()
                binding.btLogin.isEnabled = true
                binding.pBLogin.visibility = View.INVISIBLE

            }

            // retrofit builder for apis
            val userData = login(
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString(),
                "", ""
            )


            //handling api requests
            val loginRequest = rf.login(userData)

            loginRequest.enqueue(object : Callback<login> {
                override fun onResponse(call: Call<login>, response: Response<login>) {
                    if (response.code() == 200) {
                        Toast.makeText(this@Login, "Welcome to Recipe Pool", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this@Login, MainActivity::class.java)
                        editor.putString("access token", response.body()!!.access.toString())
                        editor.putString("refresh token", response.body()!!.refresh.toString())
                        editor.putString("email", binding.etEmail.text.toString())
                        editor.apply()
                        binding.btLogin.isEnabled = true
                        binding.pBLogin.visibility = View.INVISIBLE
                        startActivity(intent)
                        finish()
                    } else {
                        Log.d("Login response error", response.message().toString())
                        Toast.makeText(
                            this@Login,
                            "Some error occurred please try after some time",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btLogin.isEnabled = true
                        binding.pBLogin.visibility = View.INVISIBLE
                    }
                }

                override fun onFailure(call: Call<login>, t: Throwable) {
                    binding.pBLogin.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@Login,
                        "Please check your email id and password",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btLogin.isEnabled = true
                    Log.d("Login failure error", t.message.toString())
                    binding.pBLogin.visibility = View.INVISIBLE
                    Toast.makeText(
                        this@Login,
                        "Please check your email id or sign up",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btLogin.isEnabled = true
                    Log.d("Some sign up error occurred", t.message.toString())
                }
            })
        }


        binding.btLoginGoogle.setOnClickListener {

            binding.pBLogin.visibility = View.VISIBLE

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            21048189619-4n65kvscmm2gk0amf5gks54faro9ti7h.apps.googleusercontent.com
                .requestIdToken("21048189619-4n65kvscmm2gk0amf5gks54faro9ti7h.apps.googleusercontent.com")
                .requestEmail()
                .build()

            mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            signIn()
        }

    }

    fun signIn() {
        val signInIntent = Intent(mGoogleSignInClient.signInIntent)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val user = completedTask.getResult(ApiException::class.java)
            Log.d("google login token", user.idToken.toString())

            val tokenData = google("", "", user.idToken.toString())

            val googleRequest = rf.google(tokenData)
            googleRequest.enqueue(object : Callback<google> {
                override fun onResponse(call: Call<google>, response: Response<google>) {
                    if (response.code() == 200) {
                        Toast.makeText(
                            this@Login,
                            "Welcome to Recipe Pool",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        val intent = Intent(this@Login, MainActivity::class.java)
                        editor.putString(
                            "access token",
                            response.body()!!.access.toString()
                        )
                        editor.putString(
                            "refresh token",
                            response.body()!!.refresh.toString()
                        )
                        editor.apply()
                        binding.btLoginGoogle.isEnabled = true
                        binding.pBLogin.visibility = View.INVISIBLE
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<google>, t: Throwable) {
                    binding.btLoginGoogle.isEnabled = true
                    binding.pBLogin.visibility = View.INVISIBLE
                    Toast.makeText(this@Login, "Please try again later", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d("Message", e.toString())
            Log.d("error code", e.statusCode.toString())
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show()
        }
    }
}