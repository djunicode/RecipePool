package com.example.recipepool.screens

import android.content.ContentUris
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
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
import com.example.recipepool.RealPathUtil.RealPathUtil.getDataColumn
import com.example.recipepool.RealPathUtil.RealPathUtil.isDownloadsDocument
import com.example.recipepool.RealPathUtil.RealPathUtil.isExternalStorageDocument
import com.example.recipepool.RealPathUtil.RealPathUtil.isGooglePhotosUri
import com.example.recipepool.RealPathUtil.RealPathUtil.isMediaDocument
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.*
import com.example.recipepool.databinding.ActivityAddRecipeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterIngredientsAddRecipe
import com.example.recipepool.recycleradapter.RecyclerAdapterStepsAddRecipe
import com.example.recipepool.recycleradapter.SwipeDelete
import okhttp3.*
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
    private var postPath: String = ""
    private var requestFile: RequestBody? = null
    private var img: MultipartBody.Part? = null
    private var mediaPath = ""

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

//        binding.textNameRecipe.visibility = View.INVISIBLE
//        binding.textAboutAddRecipe.visibility = View.INVISIBLE
//        binding.editTimeAddRecipe.isEnabled = false
//        binding.editNameRecipe.isEnabled = false
//        binding.editAboutAddRecipe.isEnabled = false


//        binding.editNameRecipe.setOnClickListener {
//            binding.editNameRecipe.isEnabled = true
//        }

        binding.imageAddRecipe.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), 100)
        }

//        binding.editAboutAddRecipe.setOnClickListener {
//            binding.editAboutAddRecipe.isEnabled = true
//        }

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

//        binding.editTimeAddRecipe.setOnClickListener {
//            binding.editTimeAddRecipe.isEnabled = true
//        }

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

            var name = binding.editNameRecipe.text.toString().trim()
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
                val file = File(postPath)
//                requestFile = RequestBody.create(
//                    MediaType.parse(contentResolver.getType(uri!!)),
//                    file
//                )

//                requestFile = RequestBody.create(
//                    MediaType.parse(contentResolver.getType(uri!!)),
//                    file
//                )

                requestFile = RequestBody.create(MediaType.parse("image/*"), file)
                img = MultipartBody.Part.createFormData("image", file.name, requestFile!!)
            }


//            val recipeMap = HashMap<String, RequestBody>()
//            recipeMap["cuisine"] = RequestBody.create(MultipartBody.FORM, "chinese")
//            recipeMap["cuisineType"] = RequestBody.create(MultipartBody.FORM, "abc")
//            recipeMap["dishType"] = RequestBody.create(MultipartBody.FORM, "abc")
//            recipeMap["healthLabels"] = RequestBody.create(MultipartBody.FORM, "abc")
//            recipeMap["instructions"] = RequestBody.create(MultipartBody.FORM, desc.toString())
//            recipeMap["label"] = RequestBody.create(MultipartBody.FORM, name.toString())
//            recipeMap["mealType"] = RequestBody.create(MultipartBody.FORM, foodType.toString())
//            recipeMap["missingIngredients"] = RequestBody.create(MultipartBody.FORM, "abc")
//            recipeMap["totalNutrients"] = RequestBody.create(MultipartBody.FORM, "abc")
//            recipeMap["totalTime"] = RequestBody.create(MultipartBody.FORM, time.toString())
//
//            val iList = arrayListOf<RequestBody>()
//            val sList = arrayListOf<RequestBody>()
//
//            for (i in 0 until ingredients.size) {
//                val ig = AddRecipeIngredient(ingredients[i].name,ingredients[i].quantity)
//                iList.add(RequestBody.create(MediaType.parse("text/plain"), ig))
//            }
//
//            for (i in 0 until steps.size) {
//                sList.add(RequestBody.create(MediaType.parse("text/plain"), steps[i].steps!!))
//            }


//            recipeMap["steps_list"] = RequestBody.create(MultipartBody.FORM,"")
//
//            recipeMap["ingredient_list"] = RequestBody.create(MultipartBody.FORM,ingredients.toList())

            Log.d("ingredient list", ingredients.toList().toString())
            Log.d("steps list", steps.toList().toString())
            val recipe = AddNewRecipe(
                1,
                "chinese",
                "abc",
                foodType.toString(),
                "abc",
                ingredients.toList(),
                desc.toString(),
                name.toString(),
                0,
                "abc",
                "abc",
                steps.toList(),
                "abc",
                time.toString(),
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

                        //adding new recipe
//                        val newRecipe = rf.addRecipe(
//                            "Bearer ${response.body()!!.access.toString()}"
//                            img,
//                            recipe
//                        )

                        val newRecipe = rf.addRecipe(
                            "Bearer ${response.body()!!.access.toString()}", recipe
                        )
                        Log.d("data", recipe.toString())

//                        val head = "Bearer ${response.body()!!.access.toString()}"
//                        val nr = rf.addRecipe2(
//                            head,
//                            1,
//                            "chinese",
//                            "abc",
//                            foodType.toString(),
//                            "abc",
//                            desc.toString(),
//                            name.toString(),
//                            "abc",
//                            "abc",
//                            "abc",
//                            time.toString(),
//                            0,
//                            img!!,
//                            ingredients.toList(),
//                            steps.toList()
//                        )
//                        val nr = rf.addRecipe2(
//                            head.toString(),
//                            1,
//                            0,
//                            img!!,
//                            ingredients.toList(),
//                            steps.toList(),
//                            recipeMap
//                        )
//                        nr.enqueue(object : Callback<String> {
//                            override fun onResponse(
//                                call: Call<String>,
//                                response: Response<String>
//                            ) {
//                                if (response.code() == 202 || response.code() == 200) {
//                                    Log.d("img data", "data stored succesfully")
//                                    Log.d("img data", response.body().toString())
//                                } else {
//                                    Log.d(
//                                        "img s fail",
//                                        response.message().toString() + response.code().toString()
//                                    )
//                                }
//                            }
//
//                            override fun onFailure(call: Call<String>, t: Throwable) {
//                                Log.d(
//                                    "img failure",
//                                    response.message().toString() + response.code().toString()
//                                )
//                            }
//
//                        })

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
                                                if(response2.code() == 202 || response2.code() == 200){
                                                    binding.addRecipePG.visibility = View.INVISIBLE
                                                    val intent = Intent(this@AddRecipe, MainActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                    Log.d("img s","image uploaded successfully")
                                                    Log.d("img uri",uri.toString())

                                                }
                                                else{
                                                    binding.addRecipePG.visibility = View.INVISIBLE
                                                    val intent = Intent(this@AddRecipe, MainActivity::class.java)
                                                    startActivity(intent)
                                                    finish()
                                                    Log.d("image upload s f",response2.message().toString()+response2.code().toString())
                                                    Log.d("add recipe success", response1.message())

                                                }
                                            }

                                            override fun onFailure(
                                                call: Call<ResponseNewRecipe>,
                                                t: Throwable
                                            ) {
                                                binding.addRecipePG.visibility = View.INVISIBLE
                                                val intent = Intent(this@AddRecipe, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()
                                                Log.d("image upload failed add recipe success", t.message.toString())
                                                Log.d("img uri",uri.toString())
                                            }

                                        })
                                    }
                                    else{
                                        binding.addRecipePG.visibility = View.INVISIBLE
                                        val intent = Intent(this@AddRecipe, MainActivity::class.java)
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
                    }
                }

                override fun onFailure(call: Call<refresh>, t: Throwable) {
                    Log.d("token error", "Some error occurred while receiving tokens")
                    Log.d("token error", t.message.toString())
                }
            })

//            val addRecipeRequest = rf.addRecipe()
//            addRecipeRequest.enqueue(object : Callback<>)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                uri = data?.data
                url = uri.toString()
                Log.d("image uri",uri.toString())
                if (url.isNotEmpty()) {
                    binding.textAddImage.visibility = View.INVISIBLE
                    Glide.with(this)
                        .load(uri)
                        .into(binding.imageAddRecipe)

//                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//                    val cursor =
//                        contentResolver.query(data?.data!!, filePathColumn, null, null, null)
//                    assert(cursor != null)
//                    cursor!!.moveToFirst()

//                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
//                    mediaPath = cursor.getString(columnIndex)
//                    cursor.close()
                    postPath = getRealPathFromUri(uri!!).toString()
                    Log.d("image path", postPath.toString())

                } else {
                    Toast.makeText(
                        this,
                        "Unable to load the image please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun getRealPathFromUri(uri: Uri): String? { // function for file path from uri,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(
                this, uri
            )
        ) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
                )
                return getDataColumn(this, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(
                    split[1]
                )
                return getDataColumn(this, contentUri, selection, selectionArgs)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address
            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(
                this,
                uri,
                null,
                null
            )
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }
}