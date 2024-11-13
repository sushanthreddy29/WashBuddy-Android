package com.unh.washbuddy_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.databinding.ActivityAdminDashboardBinding

class AdminDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityAdminDashboardBinding  // Update with your actual layout binding class name
    private val db = Firebase.firestore

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var adminAdapter: AdminAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminDashboardBinding.inflate(layoutInflater)  // Inflate the layout for the activity
        setContentView(binding.root)

        supportActionBar?.title = "Admin Dashboard"

        // Initialize RecyclerView and Adapter
        mRecyclerView = binding.recyclerViewAdmin
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        val ordersRecyclerList = arrayListOf<AdminCard>()
        adminAdapter = AdminAdapter(ordersRecyclerList, this)
        mRecyclerView.adapter = adminAdapter

        // Fetch data from Firestore
        getDetailsFromDatabase()

        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            usersignin.username = ""
            usersignin.email = ""
            usersignin.firstname = ""
            usersignin.lastname = ""
            usersignin.useruid = ""
            usersignin.documentid = ""

            val intent = Intent(this, signin::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getDetailsFromDatabase()
    }

    private fun getDetailsFromDatabase() {
        val adminRecyclerList: ArrayList<AdminCard> = arrayListOf()
        mRecyclerView = binding.recyclerViewAdmin
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(baseContext)
        val adminAdapter = AdminAdapter(adminRecyclerList, this)
        mRecyclerView.adapter = adminAdapter

        db.collection("LaundryOrders")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("ActivityFragment", "Loading Data Failed", e)
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Log.d("OrdersFragment", "Data is not available")
                    return@addSnapshotListener
                }

                Log.d("OrdersFragment", "Data is available with ${snapshots.size()} entries")

                adminRecyclerList.clear()

                for (document in snapshots.documents) {
                    val address = document.getString("address") ?: "N/A"
                    val selectLaundromat = document.getString("laundromat") ?: "N/A"
                    val email = document.getString("email") ?: "N/A"
                    val date = document.getString("pickupdate") ?: "N/A"
                    val time = document.getString("pickuptime") ?: "N/A"
                    val amount = document.getString("amount") ?: "$0.00"

                    adminRecyclerList.add(
                        AdminCard(
                            "$date, $time",
                            "User Email: $email",
                            "Pickup Address: $address",
                            "Laundromat: $selectLaundromat",
                            amount
                        )
                    )
                }
                Log.d(
                    "ActivityFragment",
                    "RecyclerView updated with ${adminRecyclerList.size} items"
                )

                adminAdapter.notifyDataSetChanged()
            }
    }
}