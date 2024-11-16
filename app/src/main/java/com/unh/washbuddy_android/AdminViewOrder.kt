package com.unh.washbuddy_android

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.databinding.AdminViewOrderBinding

class AdminViewOrder : AppCompatActivity() {

    private lateinit var binding: AdminViewOrderBinding // View binding instance

    private var currentOrderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminViewOrderBinding.inflate(layoutInflater) // Inflate the layout using binding
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "View Order Details" // Optional: Set a title for the action bar
            setDisplayHomeAsUpEnabled(true) // Enable the back button
            setDisplayShowHomeEnabled(true)
        }

        // Get the AdminCard object from Intent
        val orderDetails = intent.getSerializableExtra("orderDetails") as? AdminCard

        // Populate fields with data
        orderDetails?.let {
            binding.enteraddress.setText(it.address)
            binding.enterpickupdate.setText(it.date)
            binding.enterpickuptime.setText(it.time)
            binding.enterlaundromat.setText(it.laundry)
            binding.enterdetergent.setText(it.detergent)
            binding.enterdelivery.setText(it.delivery)
            binding.smallbag.setText(it.smallbag)
            binding.regularbag.setText(it.regularbag)
            binding.enterextras.setText(it.extras)
            (binding.dropFieldStatus.editText as? AutoCompleteTextView)?.setText(it.status, false)
            binding.totalAmountTextView.text = "Total Amount: ${it.amount}"
            currentOrderId = it.orderId
        }

        binding.submit.setOnClickListener {
            val order_status = binding.enterstatus.text.toString()

            if(order_status == "Completed"){
                updateDatabase()
            }
        }
    }

    private fun updateDatabase(){
        Firebase.firestore.collection("LaundryOrders")
            .get()
            .addOnSuccessListener {orders ->
                for (order in orders) {
                    val documentOrderId = order.getString("orderId")
                    if(currentOrderId == documentOrderId){
                        val update_status = mapOf("status" to "Completed")

                        Firebase.firestore.collection("LaundryOrders")
                            .document(order.id)
                            .update(update_status)
                        Toast.makeText(this, "Order Status Updated", Toast.LENGTH_SHORT).show()

                    }
                }
            }
            .addOnFailureListener { e ->
                Log.w("LOGINERROR", "error", e)
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // Back button in the action bar
                finish() // Close the current activity and go back
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
