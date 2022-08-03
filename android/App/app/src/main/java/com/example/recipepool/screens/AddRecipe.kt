package com.example.recipepool.screens

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.*
import com.example.recipepool.databinding.ActivityAddRecipeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterIngredientsAddRecipe
import com.example.recipepool.recycleradapter.RecyclerAdapterStepsAddRecipe
import com.example.recipepool.recycleradapter.SwipeDelete
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class AddRecipe : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding
    private var url: String = ""
    private lateinit var ingredients: ArrayList<AddRecipeIngredient>
    private lateinit var steps: ArrayList<AddRecipeSteps>
    private lateinit var rvIngredients: RecyclerView
    private lateinit var rvSteps: RecyclerView
    private lateinit var editor: SharedPreferences.Editor
    private var uri: Uri? = null
    private var filePath: String = ""
    private var img: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var foodType = "None"

        // shared preferences to store user token
        val pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        editor = pref.edit()

        binding.addRecipePG.visibility = View.INVISIBLE

        rvIngredients = binding.rvIngredientsAddRecipe
        rvSteps = binding.rvStepsAddRecipes

        ingredients = arrayListOf()
        steps = arrayListOf()

        setContentView(binding.root)
        setSupportActionBar(binding.addRecipeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val ingredientAdapter = RecyclerAdapterIngredientsAddRecipe(ingredients)
        val stepAdapter = RecyclerAdapterStepsAddRecipe(steps)

        rvIngredients.apply {
            layoutManager = LinearLayoutManager(this@AddRecipe)
            adapter = ingredientAdapter
        }

        rvSteps.apply {
            layoutManager = LinearLayoutManager(this@AddRecipe)
            adapter = stepAdapter
        }


        binding.imageAddRecipe.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 1)
        }

        binding.addIconAddRecipe.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.layout_dialog_add_ingredient, null)
            builder.setCancelable(false)
            builder.setView(view)
                .setTitle("Add Ingredient")
            builder.setPositiveButton("Ok") { dialog, _ ->
                val ingre = view.findViewById<EditText>(R.id.editIngredient)
                val quant = view.findViewById<EditText>(R.id.editQuantiy)
                val data =
                    AddRecipeIngredient(
                        ingre.text.toString().trim(),
                        quant.text.toString().toInt()
                    )
                ingredients.add(data)
                rvIngredients.adapter!!.notifyItemInserted(ingredients.size - 1)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        binding.addIcon2AddRecipe.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.layout_dialog_add_step, null)
            builder.setCancelable(false)
            builder.setView(view)
                .setTitle("Add Step")
            builder.setPositiveButton("Ok") { dialog, _ ->
                val s = view.findViewById<EditText>(R.id.editStep)
                val data = AddRecipeSteps(s.text.toString().trim())
                steps.add(data)
                rvSteps.adapter!!.notifyItemInserted(steps.size - 1)
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

        val swipeDeleteIngredient = object : SwipeDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        ingredientAdapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val swipeDeleteStep = object : SwipeDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.RIGHT -> {
                        stepAdapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val touchHelperIngredients = ItemTouchHelper(swipeDeleteIngredient)
        touchHelperIngredients.attachToRecyclerView(rvIngredients)

        val touchHelperSteps = ItemTouchHelper(swipeDeleteStep)
        touchHelperSteps.attachToRecyclerView(rvSteps)

        binding.addRecipeButton.setOnClickListener {

            var add = true

            val name = binding.editNameRecipe.text.toString().trim()
            if (name.isEmpty()) {
                binding.editNameRecipe.error = "Add recipe name"
                binding.editNameRecipe.requestFocus()
                add = false
            }

            if (url == "") {
                Toast.makeText(this, "Please add an image of the recipe", Toast.LENGTH_SHORT).show()
                add = false
            }

            val desc = binding.editAboutAddRecipe.text.toString().trim()
            if (desc.isEmpty()) {
                binding.editAboutAddRecipe.error = "Add recipe description"
                binding.editAboutAddRecipe.requestFocus()
                add = false
            }

            val time = binding.editTimeAddRecipe.text.toString().trim()
            if (time.isEmpty()) {
                binding.editTimeAddRecipe.error = "Add cooking time"
                binding.editTimeAddRecipe.requestFocus()
                add = false
            }

            if (ingredients.isEmpty()) {
                Toast.makeText(this, "Add some ingredients", Toast.LENGTH_SHORT).show()
                add = false
            }

            val radio = binding.radioGroupAddRecipe.checkedRadioButtonId
            if (radio == -1) {
                Toast.makeText(this, "Select type of food", Toast.LENGTH_SHORT).show()
                add = false
            } else {
                when {
                    binding.radioVeg.id == radio -> {
                        Log.d("food type", "veg")
                        foodType = "Veg"
                    }
                    binding.radioNonVeg.id == radio -> {
                        Log.d("food type", "non veg")
                        foodType = "Non Veg"
                    }
                    binding.radioVegan.id == radio -> {
                        Log.d("food type", "vegan")
                        foodType = "Vegan"
                    }
                }
            }

            if (steps.isEmpty()) {
                Toast.makeText(this, "Add some steps", Toast.LENGTH_SHORT).show()
                add = false
            }

            if (!add) return@setOnClickListener

            binding.addRecipePG.visibility = View.VISIBLE


            if (url != "") {
                if (!filePath.equals("", ignoreCase = true)) {
                    val file = File(filePath)
                    val reqFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                    img = MultipartBody.Part.createFormData("image", file.name, reqFile)
                    Log.d("image ", img.toString())
                }
            }

            Log.d("ingredient list", ingredients.toList().toString())
            Log.d("steps list", steps.toList().toString())
            val recipe = AddNewRecipe(
                1,
                "chinese",
                "abc",
                foodType,
                "abc",
                ingredients.toList(),
                desc,
                name,
                0,
                "abc",
                "abc",
                steps.toList(),
                "abc",
                time,
            )

            val rdata = refresh("", pref.getString("refresh token", null))
            val accessToken = rf.refresh(rdata)
            accessToken.enqueue(object : Callback<refresh> {
                override fun onResponse(call: Call<refresh>, response: Response<refresh>) {
                    if (response.code() == 200) {
                        editor.putString("access token", response.body()!!.access.toString())
                        editor.putString("refresh token", response.body()!!.refresh.toString())
                        editor.apply()
                        Log.d("tokens", "Tokens received successfully")
                        Log.d("refresh token", pref.getString("refresh token", null).toString())
                        Log.d("access token", pref.getString("access token", null).toString())


                        val newRecipe = rf.addRecipe(
                            "Bearer ${response.body()!!.access.toString()}", recipe
                        )
                        Log.d("data", recipe.toString())

                        newRecipe.enqueue(object : Callback<ResponseNewRecipe> {
                            override fun onResponse(
                                call: Call<ResponseNewRecipe>,
                                response1: Response<ResponseNewRecipe>
                            ) {
                                if (response1.code() == 200 || response1.code() == 202) {
                                    Toast.makeText(
                                        this@AddRecipe,
                                        "Your recipe has been successfully saved",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if (img != null) {
                                        val newImg = rf.addImage(
                                            "Bearer ${response.body()!!.access.toString()}",
                                            response1.body()!!.id,
                                            img!!
                                        )
                                        newImg.enqueue(object : Callback<ResponseNewRecipe> {
                                            override fun onResponse(
                                                call: Call<ResponseNewRecipe>,
                                                response2: Response<ResponseNewRecipe>
                                            ) {
                                                if (response2.code() == 202 || response2.code() == 200) {
                                                    binding.addRecipePG.visibility = View.INVISIBLE
                                                    val intent = Intent(
                                                        this@AddRecipe,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                    Log.d("img s", "image uploaded successfully")
                                                    Log.d("img uri", uri.toString())
                                                    Log.d(
                                                        "recipe id",
                                                        response1.body()!!.id.toString()
                                                    )

                                                } else {
                                                    binding.addRecipePG.visibility = View.INVISIBLE
                                                    val intent = Intent(
                                                        this@AddRecipe,
                                                        MainActivity::class.java
                                                    )
                                                    startActivity(intent)
                                                    finish()
                                                    Log.d(
                                                        "image upload s f",
                                                        response2.message()
                                                            .toString() + response2.code()
                                                            .toString()
                                                    )
                                                    Log.d("add recipe success", response1.message())
                                                    Log.d(
                                                        "recipe id",
                                                        response1.body()!!.id.toString()
                                                    )

                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<ResponseNewRecipe>,
                                                t: Throwable
                                            ) {
                                                binding.addRecipePG.visibility = View.INVISIBLE
                                                val intent =
                                                    Intent(this@AddRecipe, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                                Log.d(
                                                    "image upload failed add recipe success",
                                                    t.message.toString()
                                                )
                                                Log.d("img uri", uri.toString())
                                                Log.d("recipe id", response1.body()!!.id.toString())
                                            }

                                        })
                                    } else {
                                        binding.addRecipePG.visibility = View.INVISIBLE
                                        val intent =
                                            Intent(this@AddRecipe, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                        Log.d("add recipe success", response1.message())
                                    }

                                } else {
                                    binding.addRecipePG.visibility = View.INVISIBLE
                                    Log.d(
                                        "new recipe success error",
                                        response1.message() + "   " + response1.code().toString()
                                    )
                                }
                            }

                            override fun onFailure(call: Call<ResponseNewRecipe>, t: Throwable) {
                                Toast.makeText(
                                    this@AddRecipe,
                                    "Some error occurred please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.addRecipePG.visibility = View.INVISIBLE
                                Log.d("new recipe failure", t.message.toString())
                            }

                        })

                    } else {
                        Log.d("refresh token", pref.getString("refresh token", null).toString())
                        binding.addRecipePG.visibility = View.INVISIBLE
                        Log.d("token s response", response.body().toString())
                        Log.d("token s response", response.code().toString())
                    }
                }

                override fun onFailure(call: Call<refresh>, t: Throwable) {
                    Log.d("token error", "Some error occurred while receiving tokens")
                    Log.d("token error", t.message.toString())
                }
            })

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) if (resultCode == RESULT_OK) {
            val selectedImage: Uri? = data?.data
            filePath = getPath(selectedImage)
            url = uri.toString()
            Glide.with(this)
                .load(selectedImage)
                .into(binding.imageAddRecipe)
            binding.textAddImage.visibility = View.INVISIBLE

            Log.d("uri", selectedImage.toString())
            Log.d("image path", filePath)

        }
    }


    fun getPath(uri: Uri?): String {
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor: Cursor = managedQuery(uri, projection, null, null, null)
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex)
    }

}