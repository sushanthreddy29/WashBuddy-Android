package com.unh.washbuddy_android.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.unh.washbuddy_android.databinding.FragmentViewOrderBinding

class ViewOrderFragment: Fragment() {

    private var _binding: FragmentViewOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        _binding = FragmentViewOrderBinding.inflate(inflater, container, false)

        // Retrieve data passed through arguments
        val address = arguments?.getString("order_address") ?: "N/A"
        val date = arguments?.getString("order_date") ?: "N/A"
        val time = arguments?.getString("order_time") ?: "N/A"
        val laundromat = arguments?.getString("order_laundry") ?: "N/A"
        val detergent = arguments?.getString("order_detergent") ?: "N/A"
        val deliverySpeed = arguments?.getString("order_delivery") ?: "N/A"
        val smallBag = arguments?.getString("order_smallbag") ?: "N/A"
        val regularBag = arguments?.getString("order_regularbag") ?: "N/A"
        val extras = arguments?.getString("order_extras") ?: "N/A"
        val totalAmount = arguments?.getString("order_amount") ?: "$0.00"


        // Populate the UI
        binding.enteraddress.setText(address)
        binding.enterpickupdate.setText(date)
        binding.enterpickuptime.setText(time)
        binding.enterlaundromat.setText(laundromat)
        binding.enterdetergent.setText(detergent)
        binding.enterdelivery.setText(deliverySpeed)
        binding.smallbag.setText(smallBag)
        binding.regularbag.setText(regularBag)
        binding.enterextras.setText(extras)
        binding.totalAmountTextView.text = "Total Amount: $totalAmount"

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}