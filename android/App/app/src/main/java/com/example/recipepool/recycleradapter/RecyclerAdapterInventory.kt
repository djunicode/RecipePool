package com.example.recipepool.recycleradapter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.recipepool.R
import com.example.recipepool.constants.ApiConstants.rf
import com.example.recipepool.data.Inventory
import com.example.recipepool.data.refresh
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecyclerAdapterInventory(val data: ArrayList<Inventory>, val context: Context) :
    RecyclerView.Adapter<RecyclerAdapterInventory.ViewHolder>() {

    // shared preferences to store user token
    val pref: SharedPreferences = context.getSharedPreferences("SharedPref", MODE_PRIVATE)
    val editor: SharedPreferences.Editor = pref.edit()

    fun deleteItem(i: Int) {
        deleteItem(data[i])
        data.removeAt(i)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.layout_inventory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val input = data[position]
        holder.bind(input)

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.layout_dialog_add_ingredient, null)
            val item = view.findViewById<EditText>(R.id.editIngredient)
            val quant = view.findViewById<EditText>(R.id.editQuantiy)
            item.hint = "Enter Ingredient"
            item.setText(data[position].ingredient_name.toString())
            quant.setText(data[position].quantity.toString())
            item.isEnabled = false
            builder.setView(view)
                .setTitle("Add Item")
            builder.setPositiveButton("Save") { dialog, _ ->
                if (quant.text.isNotEmpty()) {
                    val d = Inventory(
                        data[position].id,
                        data[position].ingredient_name.toString(),
                        quant.text.toString().toFloat(),
                        data[position].ingredient.toString(),
                        data[position].user,
                    )
                    Log.d("update data",d.toString())
                    data[position] = d
                    updateItem(d)
                }
                dialog.dismiss()
            }
            builder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            builder.show()
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val itemName: TextView = v.findViewById(R.id.text_item_name)
        private val itemQty: TextView = v.findViewById(R.id.text_item_qty)
        fun bind(data: Inventory) {
            itemName.text = data.ingredient_name.toString()
            itemQty.text = data.quantity.toString()
        }
    }


    private fun deleteItem(data: Inventory) {
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

                    val deleteInventory = data.id?.let {
                        rf.deleteInventory(
                            "Bearer ${response.body()!!.access.toString()}", it
                        )
                    }

                    deleteInventory?.enqueue(object : Callback<String> {
                        override fun onResponse(
                            call: Call<String>,
                            response: Response<String>
                        ) {
                            if (response.code() == 202) {
                                Log.d("item delete status", "item deleted successfully")
                                Toast.makeText(
                                    context,
                                    "Item deleted from inventory",
                                    Toast.LENGTH_SHORT
                                )
                                notifyDataSetChanged()
                            } else {
                                Log.d("delete inventory error", "could not delete from inventory")
                                Log.d("response error", response.message().toString())
                                Log.d("response code", response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<String>, t: Throwable) {
                            Log.d("delete item status", "item delete failure")
                            Log.d("add item error", t.message.toString())
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

    private fun updateItem(data: Inventory) {
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


                    val updateInventory = data.id?.let {
                        rf.updateInventory(
                            "Bearer ${response.body()!!.access.toString()}", it, data
                        )
                    }

                    updateInventory?.enqueue(object : Callback<Inventory> {
                        override fun onResponse(
                            call: Call<Inventory>,
                            response: Response<Inventory>
                        ) {
                            if (response.code() == 202) {
                                Log.d("update item status", "item updated successfully")
                                notifyDataSetChanged()
                            } else {
                                Log.d("update inventory error", "could not update the inventory")
                                Log.d("response error", response.message().toString())
                                Log.d("response code", response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<Inventory>, t: Throwable) {
                            Log.d("update item status", "item update failure")
                            Log.d("update item error", t.message.toString())
                            Toast.makeText(
                                context,
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