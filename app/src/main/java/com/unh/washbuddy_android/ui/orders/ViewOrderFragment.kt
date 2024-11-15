package com.unh.washbuddy_android.ui.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.unh.washbuddy_android.databinding.FragmentViewOrderBinding

class ViewOrderFragment: Fragment() {

    private var _binding: FragmentViewOrderBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        _binding = FragmentViewOrderBinding.inflate(inflater, container, false)

        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Order Details"
        }

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
        val status = arguments?.getString("order_status") ?: "N/A"


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
        binding.status.setText(status)
        binding.totalAmountTextView.text = "Total Amount: $totalAmount"

        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp() // Handle back navigation
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}