package com.unh.washbuddy_android.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.unh.washbuddy_android.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment() {

private var _binding: FragmentOrdersBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

    private lateinit var mRecyclerView: RecyclerView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val dashboardViewModel =
            ViewModelProvider(this).get(OrdersViewModel::class.java)

    _binding = FragmentOrdersBinding.inflate(inflater, container, false)
    val root: View = binding.root
/*
    val textView: TextView = binding.textDashboard
    dashboardViewModel.text.observe(viewLifecycleOwner) {
      textView.text = it
    }*/

      val ordersRecyclerList: ArrayList<OrdersCard> = ArrayList()

      for(orders in ordersList){
          ordersRecyclerList.add(
              OrdersCard(
                  orders.address,
                  orders.selectLaundromat,
                  orders.speed
              )
          )
      }

      mRecyclerView = binding.recyclerViewOrders
      mRecyclerView.setHasFixedSize(true)
      mRecyclerView.layoutManager = LinearLayoutManager(context)
      mRecyclerView.adapter = OrdersAdapter(ordersRecyclerList, this)


    return root
  }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}