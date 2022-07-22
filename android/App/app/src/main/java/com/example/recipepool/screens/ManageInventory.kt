package com.example.recipepool.screens

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Inventory
import com.example.recipepool.data.refresh
import com.example.recipepool.databinding.ActivityInventoryBinding
import com.example.recipepool.recycleradapter.RecyclerAdapterInventory
import com.example.recipepool.recycleradapter.SwipeDelete
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageInventory : AppCompatActivity() {

    private lateinit var binding: ActivityInventoryBinding
    private lateinit var inventory: ArrayList<Inventory>
    private lateinit var rvInventory: RecyclerView
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var pref: SharedPreferences
    private lateinit var inventoryAdapter:RecyclerAdapterInventory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // shared preferences to store user token
        pref = applicationContext.getSharedPreferences("SharedPref", MODE_PRIVATE)
        editor = pref.edit()

        inventory = arrayListOf()
        rvInventory = binding.rvInventory

        binding.inventoryPG.visibility = View.VISIBLE

        rvInventory.apply {
            layoutManager = LinearLayoutManager(this@ManageInventory)
        }
        getInventory()

//        rvInventory.adapter!!.notifyDataSetChanged()

//        if(inventory.isEmpty()){
//            binding.inventoryStatus.visibility = View.VISIBLE
//            binding.inventoryPG.visibility = View.INVISIBLE
//        }else{
//            binding.inventoryStatus.visibility = View.INVISIBLE
//            binding.inventoryPG.visibility = View.INVISIBLE
//        }

        binding.addItem.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.layout_dialog_add_ingredient, null)
            builder.setCancelable(false)
            builder.setView(view)
                .setTitle("Add Item")
            builder.setPositiveButton("Save") { dialog, _ ->
                val item = view.findViewById<EditText>(R.id.editIngredient)
                val quant = view.findViewById<EditText>(R.id.editQuantiy)
                item.hint = "Enter Item"
                val data =
                    Inventory(0, item.text.toString(), quant.text.toString().toFloat(), item.text.toString(), null)
                addNewItem(data)
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
                        inventoryAdapter.deleteItem(viewHolder.adapterPosition)
                    }
                }
            }
        }

        val touchHelperIngredients = ItemTouchHelper(swipeDeleteIngredient)
        touchHelperIngredients.attachToRecyclerView(rvInventory)


    }

    private fun getInventory() {
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

                    val getInventory =
                        rf.getInventory("Bearer ${response.body()!!.access.toString()}", 0)

                    getInventory.enqueue(object : Callback<ArrayList<Inventory>> {
                        override fun onResponse(
                            call: Call<ArrayList<Inventory>>,
                            response: Response<ArrayList<Inventory>>
                        ) {
                            if (response.code() == 202 || response.code() == 200) {
                                inventory = response.body()!!
                                inventoryAdapter = RecyclerAdapterInventory(inventory,this@ManageInventory)
                                rvInventory.adapter = inventoryAdapter
                                rvInventory.adapter!!.notifyDataSetChanged()
                                Log.d("inventory data",response.body().toString())
                                Log.d("inventory",inventory[0].toString())
                                if (inventory.isEmpty()) {
                                    binding.inventoryStatus.visibility = View.VISIBLE
                                    binding.inventoryPG.visibility = View.INVISIBLE
                                } else {
                                    binding.inventoryStatus.visibility = View.INVISIBLE
                                    binding.inventoryPG.visibility = View.INVISIBLE
                                }

                            } else {

                                if (inventory.isEmpty()) {
                                    binding.inventoryStatus.visibility = View.VISIBLE
                                    binding.inventoryPG.visibility = View.INVISIBLE
                                } else {
                                    binding.inventoryStatus.visibility = View.INVISIBLE
                                    binding.inventoryPG.visibility = View.INVISIBLE
                                }

                                Log.d("getting inventory error", "could not get the inventory")
                                Log.d("response error", response.message().toString())
                                Log.d("response code", response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<ArrayList<Inventory>>, t: Throwable) {
                            Log.d("getting item status", "getting inventory failed")
                            Log.d("getting inventory error", t.message.toString())
                            Toast.makeText(
                                this@ManageInventory,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            )
                        }
                    })

                } else {
                    Log.d("refresh token", pref.getString("refresh token", null).toString())
                    Log.d("token s response", response.body().toString())
                    Log.d("token code", response.code().toString())
                }
            }

            override fun onFailure(call: Call<refresh>, t: Throwable) {
                Log.d("token error", "Some error occurred while receiving tokens")
                Log.d("token error", t.message.toString())
            }

        })
    }

    private fun addNewItem(data: Inventory) {
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

                    val addInventory = rf.addInventory(
                        "Bearer ${response.body()!!.access.toString()}", 0, data
                    )

                    addInventory.enqueue(object : Callback<Inventory> {
                        override fun onResponse(
                            call: Call<Inventory>,
                            response: Response<Inventory>
                        ) {
                            if (response.code() == 202) {
                                inventory.add(data)
                                rvInventory.adapter!!.notifyItemInserted(inventory.size - 1)
                                Log.d("new item added status", "new item added successfully")
                                Toast.makeText(
                                    this@ManageInventory,
                                    "Item added to inventory",
                                    Toast.LENGTH_SHORT
                                )
                                if (inventory.isEmpty()) {
                                    binding.inventoryStatus.visibility = View.VISIBLE
                                    binding.inventoryPG.visibility = View.INVISIBLE
                                } else {
                                    binding.inventoryStatus.visibility = View.INVISIBLE
                                    binding.inventoryPG.visibility = View.INVISIBLE
                                }
                            } else {
                                Log.d("adding inventory error", "could not add to inventory")
                                Log.d("response error", response.message().toString())
                                Log.d("response code", response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<Inventory>, t: Throwable) {
                            Log.d("new item added status", "new item not adding failed")
                            Log.d("add item error", t.message.toString())
                            Toast.makeText(
                                this@ManageInventory,
                                "Please try again",
                                Toast.LENGTH_SHORT
                            )
                        }

                    })

                } else {
                    Log.d("refresh token", pref.getString("refresh token", null).toString())
                    Log.d("token s response", response.body().toString())
                }

            }

            override fun onFailure(call: Call<refresh>, t: Throwable) {
                Log.d("token error", "Some error occurred while receiving tokens")
                Log.d("token error", t.message.toString())
            }
        })
    }
}




