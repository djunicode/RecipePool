package com.example.recipepool.screens

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Ingredients
import com.example.recipepool.databinding.ActivityAddRecipeBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterIngredientsAddRecipe
import com.example.recipepool.recycleradapter.RecyclerAdapterStepsAddRecipe
import com.example.recipepool.recycleradapter.SwipeDelete
import com.google.android.material.internal.ContextUtils.getActivity

class AddRecipe : AppCompatActivity() {

    private lateinit var binding: ActivityAddRecipeBinding
    private lateinit var url: String
    private lateinit var ingredients: ArrayList<Ingredients>
    private lateinit var steps: ArrayList<String>
    private lateinit var rvIngredients:RecyclerView
    private lateinit var rvSteps:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddRecipeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var foodType = "None"

        rvIngredients = binding.rvIngredientsAddRecipe
        rvSteps = binding.rvStepsAddRecipes

        ingredients = arrayListOf()
        steps = arrayListOf()

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

        binding.textNameRecipe.visibility = View.INVISIBLE
        binding.textAboutAddRecipe.visibility = View.INVISIBLE
        binding.editTimeAddRecipe.isEnabled = false
        binding.editNameRecipe.isEnabled = false
        binding.editAboutAddRecipe.isEnabled = false


        binding.editNameRecipe.setOnClickListener {
            binding.editNameRecipe.isEnabled = true
        }

        binding.imageAddRecipe.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), 100)
        }

        binding.editAboutAddRecipe.setOnClickListener {
            binding.editAboutAddRecipe.isEnabled = true
        }

        binding.addIconAddRecipe.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.layout_dialog_add_ingredient, null)
            builder.setCancelable(false)
            builder.setView(view)
                .setTitle("Add Ingredient")
            builder.setPositiveButton("Ok") { _, _ ->
                val ingre = view.findViewById<EditText>(R.id.editIngredient)
                val quant = view.findViewById<EditText>(R.id.editQuantiy)
                val data =
                    Ingredients(quant.text.toString().trim(), "", ingre.text.toString().trim())
                ingredients.add(data)
                rvIngredients.adapter!!.notifyItemInserted(ingredients.size - 1)
                finish()
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            builder.show()
        }

        binding.editTimeAddRecipe.setOnClickListener {
            binding.editTimeAddRecipe.isEnabled = true
        }

        binding.addIcon2AddRecipe.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.layout_dialog_add_step, null)
            builder.setCancelable(false)
            builder.setView(view)
                .setTitle("Add Step")
            builder.setPositiveButton("Ok") { _, _ ->
                val s = view.findViewById<EditText>(R.id.editStep)
                val data = s.text.toString().trim()
                steps.add(data)
                rvSteps.adapter!!.notifyItemInserted(steps.size-1)
                finish()
            }
            builder.setNegativeButton("Cancel") { _, _ ->
                finish()
            }
        }

        val swipeDeleteIngredient = object :SwipeDelete(){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
                    ItemTouchHelper.RIGHT -> {
                        ingredientAdapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val swipeDeleteStep = object :SwipeDelete() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction){
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

            if (url.isEmpty()) {
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
                add= false
            }

            if(ingredients.isEmpty()){
                Toast.makeText(this,"Add some ingredients",Toast.LENGTH_SHORT).show()
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

            if(steps.isEmpty()){
                Toast.makeText(this,"Add some steps",Toast.LENGTH_SHORT).show()
                add = false
            }

            if(!add) return@setOnClickListener

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {
                val uri: Uri? = data?.data
                url = uri.toString()
                if (url.isNotEmpty()) {
                    Glide.with(this)
                        .load(uri)
                        .into(binding.imageAddRecipe)
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
}