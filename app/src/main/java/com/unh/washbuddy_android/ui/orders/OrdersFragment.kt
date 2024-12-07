package com.unh.washbuddy_android.ui.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unh.washbuddy_android.R
import com.unh.washbuddy_android.databinding.FragmentOrdersBinding
import com.unh.washbuddy_android.AppData
import java.text.SimpleDateFormat
import java.util.Locale

class OrdersFragment : Fragment(), OnOrderButtonClickListener  {

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
        val dashboardViewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)

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

        val email = AppData.email
        val ordersRecyclerList: ArrayList<OrdersCard> = arrayListOf()
        mRecyclerView = binding.recyclerViewOrders
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.layoutManager = LinearLayoutManager(context)
        val ordersAdapter = OrdersAdapter(ordersRecyclerList, this)
        mRecyclerView.adapter = ordersAdapter

        db.collection("LaundryOrders")
            .whereEqualTo("email",email)
            .addSnapshotListener { s, e ->
                if (e != null) {
                    Log.e("ActivityFragment", "Loading Data Failed", e)
                    return@addSnapshotListener
                }

                if (s == null || s.isEmpty) {
                    Log.d("OrdersFragment", "Data is not available")
                    return@addSnapshotListener
                }

                Log.d("OrdersFragment", "Data is available with ${s.size()} entries")

                ordersRecyclerList.clear()

                for (document in s.documents) {
                    val address = document.getString("address") ?: "N/A"
                    val selectLaundromat = document.getString("laundromat") ?: "N/A"
                    val speed = document.getString("speed") ?: "N/A"
                    val date = document.getString("pickupdate") ?: "N/A"
                    val time = document.getString("pickuptime") ?: "N/A"
                    val amount = document.getString("amount") ?: "$0.00"
                    val status = document.getString("status") ?: "N/A"
                    val detergent = document.getString("detergent") ?: "N/A"
                    val smallbag = document.getString("smallbag") ?: "N/A"
                    val regularbag = document.getString("regularbag") ?: "N/A"
                    val extras = document.getString("extras") ?: "N/A"
                    val laundryaddress = document.getString("laundryaddress") ?: "N/A"

                    ordersRecyclerList.add(
                        OrdersCard(
                            date,
                            time,
                            address,
                            selectLaundromat,
                            speed,
                            amount,
                            status,
                            detergent,
                            smallbag,
                            regularbag,
                            extras,
                            laundryaddress
                        )
                    )
                }

                ordersRecyclerList.sortByDescending { order ->
                    try {
                        SimpleDateFormat("MMM dd yyyy, h:mm a", Locale.getDefault()).parse("${order.date}, ${order.time}")
                    } catch (e: Exception) {
                        null // Handle invalid date format
                    }
                }

                Log.d(
                    "ActivityFragment",
                    "RecyclerView updated with ${ordersRecyclerList.size} items"
                )
                ordersAdapter.notifyDataSetChanged()

            }
    }

    override fun onViewOrderClick(order: OrdersCard) {
        val bundle = Bundle().apply {
            putString("order_date", order.date)
            putString("order_time", order.time)
            putString("order_address", order.address)
            putString("order_laundry", order.laundry)
            putString("order_delivery", order.delivery)
            putString("order_amount", order.amount)
            putString("order_status", order.status)
            putString("order_smallbag", order.smallbag)
            putString("order_regularbag", order.regularbag)
            putString("order_detergent", order.detergent)
            putString("order_extras", order.extras)
            putString("order_laundryaddress", order.laundryadress)
        }
        findNavController().navigate(R.id.viewOrderFragment, bundle)
        //Log.d("OrdersFragment", "View order clicked for order: $order")
        // Implement navigation or other actions here
    }

    override fun onReorderClick(order: OrdersCard) {
        // Handle the reorder action
        val bundle = Bundle().apply {
            putString("order_date", order.date)
            putString("order_time", order.time)
            putString("order_address", order.address)
            putString("order_laundry", order.laundry)
            putString("order_delivery", order.delivery)
            putString("order_amount", order.amount)
            putString("order_smallbag", order.smallbag)
            putString("order_regularbag", order.regularbag)
            putString("order_detergent", order.detergent)
            putString("order_extras", order.extras)
            putString("order_laundryaddress", order.laundryadress)
        }
        findNavController().navigate(R.id.reorderFragment2, bundle)
        //Log.d("OrdersFragment", "Reorder clicked for order: $order")
        // Implement reorder action here
    }

}

