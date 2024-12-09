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

// Some parts of this code were generated with the assistance of ChatGPT by OpenAI.
// This includes implementations related to [briefly describe the feature or functionality, e.g., "RecyclerView item click handling" or "Firebase data retrieval"].

class AdminViewOrder : AppCompatActivity() {

    private lateinit var binding: AdminViewOrderBinding // View binding instance

    private var currentOrderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AdminViewOrderBinding.inflate(layoutInflater) // Inflate the layout using binding
        setContentView(binding.root)

        supportActionBar?.apply {
            title = "View Order Details"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        val orderDetails = intent.getSerializableExtra("orderDetails") as? AdminCard

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
            binding.enterlaundryaddress.setText(it.laundryaddress)
            (binding.dropFieldStatus.editText as? AutoCompleteTextView)?.setText(it.status, false)
            binding.totalAmountTextView.text = "Total Amount: ${it.amount}"
            currentOrderId = it.orderId
        }

        if(binding.enterstatus.text.toString().trim() == "Completed"){
            binding.enterstatus.setText("Completed")
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
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
