package com.unh.washbuddy_android.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val db = Firebase.firestore

    private lateinit var mRecyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(OrdersViewModel::class.java)

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getDetailsFromDatabase()
    }

    override fun onResume() {
        super.onResume()
        getDetailsFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getDetailsFromDatabase() {

        val ordersRecyclerList: ArrayList<OrdersCard> = arrayListOf()
        mRecyclerView = binding.recyclerViewOrders
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        val ordersAdapter = OrdersAdapter(ordersRecyclerList, this)
        mRecyclerView.adapter = ordersAdapter

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

                ordersRecyclerList.clear()

                for (document in snapshots.documents) {
                    val address = document.getString("address") ?: "N/A"
                    val selectLaundromat = document.getString("laundromat") ?: "N/A"
                    val speed = document.getString("speed") ?: "N/A"

                    ordersRecyclerList.add(
                        OrdersCard(
                            "Pickup Address: $address",
                            "Laundromat: $selectLaundromat",
                            "Delivery Speed: $speed"
                        )
                    )
                }
                Log.d(
                    "ActivityFragment",
                    "RecyclerView updated with ${ordersRecyclerList.size} items"
                )
                ordersAdapter.notifyDataSetChanged()

            }
    }
}
